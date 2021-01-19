package com.example.diploma.service;

import com.example.diploma.data.request.GlobalSettingsRequest;
import com.example.diploma.data.response.GlobalSettingResponse;
import com.example.diploma.data.response.StatisticResponse;
import com.example.diploma.enums.GlobalSettings;
import com.example.diploma.enums.StatisticsType;
import com.example.diploma.exception.ApiError;
import com.example.diploma.exception.BadRequestException;
import com.example.diploma.model.GlobalSetting;
import com.example.diploma.model.enums.Permission;
import com.example.diploma.repository.GlobalSettingRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.List;

import static com.example.diploma.enums.GlobalSettings.Code.*;

@Service
@AllArgsConstructor
public class GlobalSettingService {
    private final GlobalSettingRepository repository;
    private final UserService userService;

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    public ResponseEntity<GlobalSettingResponse> getAllSettings2() {
        GlobalSettingResponse response = new GlobalSettingResponse();

        HashSet<GlobalSetting> settings = repository.selectGlobalSettings();

        for (GlobalSetting gs : settings) {
            boolean val = gs.getValue().getValue();
            if (gs.getCode() == STATISTICS_IS_PUBLIC) {
                response.setStatisticsIsPublic(val);
            } else if (gs.getCode() == MULTIUSER_MODE) {
                response.setMultisuserMode(val);
            } else if (gs.getCode() == POST_PREMODERATION) {
                response.setPostPremoderation(val);
            }
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private static GlobalSetting generateDefaltGS(){
        GlobalSetting gs = new GlobalSetting();
        gs.setValue(GlobalSettings.Value.YES);
        gs.setCode(STATISTICS_IS_PUBLIC);
        gs.setName(STATISTICS_IS_PUBLIC.getName());
        return gs;
    }

    public ResponseEntity<StatisticResponse> getStatistics(StatisticsType type){
        StatisticResponse response = new StatisticResponse();

        GlobalSetting pubStatistic = repository.findByCode(STATISTICS_IS_PUBLIC).orElse(generateDefaltGS());

        if (type.equals(StatisticsType.ALL)) {
            if (pubStatistic.getValue() == GlobalSettings.Value.YES ||
                    userService.getUserPermission(Permission.MODERATE)) {
                response = getStatisticsFromDB(type);
            } else {
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        }

        if (type.equals(StatisticsType.MY)){
            response = getStatisticsFromDB(type);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private StatisticResponse getStatisticsFromDB(StatisticsType type) {
        StatisticResponse response;

        String userCriteria = "";
        String andUserCriteria = "";
        if (!userService.getUserPermission(Permission.USER)) {
            throw new BadRequestException(new ApiError());
        }
        if (type.equals(StatisticsType.MY)) {
            userCriteria = " WHERE user_id = " + userService.getCurrentUser().getId();
            andUserCriteria = userCriteria.replace("WHERE", "AND");
        }
        String query = "SELECT " +
                "(SELECT SUM(view_count) FROM posts" + userCriteria + ") AS viewsCount, " +
                "(SELECT COUNT(id) FROM posts" + userCriteria + ") AS postsCount, " +
                "(SELECT COUNT(id) FROM post_votes WHERE value < 0 " + andUserCriteria + ") AS dislikesCount, " +
                "(SELECT COUNT(id) FROM post_votes WHERE value > 0"  + andUserCriteria + ") AS likesCount, " +
                "DATE_FORMAT(time,'%Y-%m-%dT%H:%m:%s') AS firstPublication " +
                "FROM posts " + userCriteria +
                " ORDER BY time ASC " +
                "LIMIT 1";

        List<StatisticResponse> item = jdbcTemplate.query(query, new RowMapper<StatisticResponse>(){

            @Override
            public StatisticResponse mapRow(ResultSet rs, int i) throws SQLException {
                StatisticResponse statisticResponse = new StatisticResponse();

                statisticResponse.setLikesCount(rs.getLong("likesCount"));
                statisticResponse.setDislikesCount(rs.getLong("dislikesCount"));
                statisticResponse.setViewsCount(rs.getLong("viewsCount"));
                statisticResponse.setFirstPublication(LocalDateTime.parse(rs.getString("firstPublication")).toEpochSecond(ZoneOffset.UTC));
                statisticResponse.setPostsCount(rs.getLong("postsCount"));
                return statisticResponse;
            }
        });

        response = item.get(0);

        return response;
    }

    public void setGlobalSettings(GlobalSettingsRequest request) {
        updateSettings(MULTIUSER_MODE, request.getMultiuserMode());
        updateSettings(POST_PREMODERATION, request.getPostPremoderation());
        updateSettings(STATISTICS_IS_PUBLIC, request.getStatisticsIsPublic());
    }

    private void updateSettings(GlobalSettings.Code code, boolean valueToUpdate) {
        GlobalSettings.Value value = valueToUpdate ? GlobalSettings.Value.YES : GlobalSettings.Value.NO;
        GlobalSetting globalSetting = repository.findByCode(code).orElse(generateDefaltGS());
        if (!value.equals(globalSetting.getValue())){
            globalSetting.setValue(value);
            repository.save(globalSetting);
        }
    }
}

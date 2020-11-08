package com.example.diploma.service;

import com.example.diploma.dto.SettingsValues;
import com.example.diploma.dto.StatisticResponse;
import com.example.diploma.enums.GlobalSettings;
import com.example.diploma.model.GlobalSetting;
import com.example.diploma.repository.GlobalSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class GlobalSettingService {
    private final GlobalSettingRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public GlobalSettingService(GlobalSettingRepository repository){
        this.repository = repository;
    }

    public SettingsValues getAllSettings(){
        SettingsValues settings = new SettingsValues();

        repository.findAll().forEach(setting -> {
            GlobalSetting MULTIUSER_MODE = repository.findByCode(GlobalSettings.Code.MULTIUSER_MODE)
                    .orElse(generateDefaltGS());
            GlobalSetting POST_PREMODERATION = repository.findByCode(GlobalSettings.Code.POST_PREMODERATION)
                    .orElse(generateDefaltGS());
            GlobalSetting STATISTICS_IS_PUBLIC = repository.findByCode(GlobalSettings.Code.STATISTICS_IS_PUBLIC)
                    .orElse(generateDefaltGS());
            settings.setMultiuserMode(MULTIUSER_MODE.getValue().getValue());
            settings.setPostPremoderation(POST_PREMODERATION.getValue().getValue());
            settings.setStatisticsIsPublic(STATISTICS_IS_PUBLIC.getValue().getValue());
        });

        return settings;
    }

    private static GlobalSetting generateDefaltGS(){
        GlobalSetting gs = new GlobalSetting();
        gs.setValue(GlobalSettings.Value.YES);
        gs.setCode(GlobalSettings.Code.STATISTICS_IS_PUBLIC);
        gs.setName(GlobalSettings.Code.STATISTICS_IS_PUBLIC.getName());
        return gs;
    }

    public ResponseEntity<StatisticResponse> getStatistics(){
        StatisticResponse response = new StatisticResponse();

        GlobalSetting pubStatistic = repository.findByCode(GlobalSettings.Code.STATISTICS_IS_PUBLIC).orElse(generateDefaltGS());
        if (pubStatistic.getValue() == GlobalSettings.Value.YES) {
            response = getStatisticsFromDB();
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    private StatisticResponse getStatisticsFromDB() {
        StatisticResponse response;

        String query = "SELECT " +
                "(SELECT SUM(view_count) FROM posts) AS viewsCount, " +
                "(SELECT COUNT(pid) FROM posts) AS postsCount, " +
                "(SELECT COUNT(id) FROM post_votes WHERE value < 0) AS dislikesCount, " +
                "(SELECT COUNT(id) FROM post_votes WHERE value > 0) AS likesCount, " +
                "ptime AS firstPublication " +
                "FROM posts " +
                "ORDER BY ptime ASC " +
                "LIMIT 1";

        List<StatisticResponse> item = jdbcTemplate.query(query, new RowMapper<StatisticResponse>(){

            @Override
            public StatisticResponse mapRow(ResultSet rs, int i) throws SQLException {
                StatisticResponse statisticResponse = new StatisticResponse();

                statisticResponse.setLikesCount(rs.getLong("likesCount"));
                statisticResponse.setDislikesCount(rs.getLong("dislikesCount"));
                statisticResponse.setViewsCount(rs.getLong("viewsCount"));
                statisticResponse.setFirstPublication(rs.getTimestamp("firstPublication").getTime());
                statisticResponse.setPostsCount(rs.getLong("postsCount"));

                return statisticResponse;
            }
        });

        response = item.get(0);

        return response;
    }
}

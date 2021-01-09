package com.example.diploma.service;

import com.example.diploma.data.response.GlobalSettingResponse;
import com.example.diploma.data.response.StatisticResponse;
import com.example.diploma.enums.GlobalSettings;
import com.example.diploma.model.GlobalSetting;
import com.example.diploma.repository.GlobalSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static com.example.diploma.enums.GlobalSettings.Code.*;

@Service
public class GlobalSettingService {
    private final GlobalSettingRepository repository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager em; //TODO удалить. инъекция для тестов

    public GlobalSettingService(GlobalSettingRepository repository){
        this.repository = repository;
    }

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

    public ResponseEntity<StatisticResponse> getStatistics(){
        StatisticResponse response = new StatisticResponse();

        GlobalSetting pubStatistic = repository.findByCode(STATISTICS_IS_PUBLIC).orElse(generateDefaltGS());
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
                "(SELECT COUNT(id) FROM posts) AS postsCount, " +
                "(SELECT COUNT(id) FROM post_votes WHERE value < 0) AS dislikesCount, " +
                "(SELECT COUNT(id) FROM post_votes WHERE value > 0) AS likesCount, " +
                "time AS firstPublication " +
                "FROM posts " +
                "ORDER BY time ASC " +
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

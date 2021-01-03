package com.example.diploma.repository;

import com.example.diploma.enums.GlobalSettings;
import com.example.diploma.model.GlobalSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.HashSet;
import java.util.Optional;

public interface GlobalSettingRepository
        extends CrudRepository<GlobalSetting, Integer> {
    Optional<GlobalSetting> findByCode(GlobalSettings.Code code);

    @Query("SELECT gs FROM GlobalSetting gs")
    HashSet<GlobalSetting> selectGlobalSettings();
}

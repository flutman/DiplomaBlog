INSERT INTO `users` (`name`, `email`, `password`, `is_moderator`, `reg_time`)
    VALUES
        ('admin', 'admin@admin.ru', '123456', 1, NOW()),
        ('user', 'user@user.ru','654321', 0, NOW());
INSERT INTO `global_settings` (`code`, `name`, `value`)
    VALUES
        ('MULTIUSER_MODE','Многопользовательский режим', 'YES'),
        ('POST_PREMODERATION','Премодерация постов', 'YES'),
        ('STATISTICS_IS_PUBLIC','Показывать всем статистику блога', 'YES');

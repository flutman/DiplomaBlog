INSERT INTO `posts` (`is_active`, `moderation_status`, `moderator_id`, `puser_id`, `ptime` , `title` , `ptext`, `view_count`)
    VALUES
        (1, 'ACCEPTED', 1, 1, NOW(), 'Первый пост', 'Текст первого поста', 10),
        (1, 'ACCEPTED', 1, 1, NOW(), 'Второй пост', 'Текст второго поста', 5);
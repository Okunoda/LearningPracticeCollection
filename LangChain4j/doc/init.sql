create table chat_message(
                             id bigint primary key auto_increment,
                             message text,
                             user_id bigint,
                             `role` varchar(36)
);
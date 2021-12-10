-- --------------------------------------------------------
--
-- Дамп данных таблицы users
--
INSERT INTO "user" (id, login, password, about, points, avatar_url)
VALUES (1, 'nxmind', '$2a$12$oNhjN/BPl5VJKGf7Kd62a.VauEXIkVBKR1Ne8UcpIr5qNir3s59xG', '', 10000, 'default.png'),
       (2, 'kamron', '$2a$12$tthTu4NyOPeRI9u.PeXsZ.DXIuBH4jquJUOyhuLVITDixsdTzWEMK', '', 10000, 'default.png');

INSERT INTO role (id, name)
VALUES (1, 'USER'),
       (2, 'ADMIN');

INSERT INTO user_role(user_id, role_id)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 2);

alter sequence user_id_seq restart with 3;


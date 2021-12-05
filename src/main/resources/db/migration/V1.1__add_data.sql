--
-- Дамп данных таблицы tags
--
INSERT INTO tag (id, tag)
VALUES (1, 'Абстракция'),
       (2, 'Кубизм'),
       (3, 'Цветное');
-- --------------------------------------------------------
--
-- Дамп данных таблицы users
--
INSERT INTO "user" (id, login, password, about, points, role, avatar_url)
VALUES (1, 'nxmind', '$2y$10$3QWnwRUUVVA7N9rcZs5lj.obeX7ALEmNLCeqseuoLecXdom1dlmJm', '', 1200, 'USER', 'default.png'),
       (2, 'sdkldfjskdl', '$2y$10$Dop5qRftqcvRL/6kGyqDpeeWSuWHjEIyCZelu2oSK6uImnpSWnFIC', '', 800, 'USER',
        'default.png'),
       (3, 'nxmind1', '$2y$10$tl6uZkjVR/NOZn9e5cFO0u4qj891G0I6UHdOjg.RYGOq6h6KxUQTy', 'Обо мне ты знаешь', 1000, 'USER',
        'default.png'),
       (4, 'deny', '$2y$10$R.gvAgTCb7Kc.wo.p1LnneW.Z4qR7TvkuenzbRFXneIsol2svFP8S', 'Здравствуйте', 1010, 'USER',
        'default.png');

--
-- Дамп данных таблицы images
--
INSERT INTO image (id, title, hash, creator_id, description, owner_id, is_activated, url, upload_date)
VALUES (1, 'Шестеренки в машине', '416c9fbc93b5158dfc317dbb67795a44', 1, 'jhsjkdfhsdkjfhsjkfhkj', 2, true,
        '1.jpg', '2021-11-27 23:05:37'),
       (2, 'sjfkldjfkl', 'bfc97e2af1bd1d08b1f073685ac95a1e', 1, 'jlksddjfsdjflksdjflksdjf', 1, true,
        '2.jpg', '2021-11-27 23:06:00'),
       (3, 'RED RED BLUE', 'de66608bb24fc4729127b355d15f0d1d', 4, 'BLUE BLUE LBUE', 4, true, '3.jpg',
        '2021-11-29 01:21:34'),
       (4, 'MON_IIIIIKA', '3cc9769df3fed7eed6573cd2fa3bb259', 2, 'JFDKFDJKFJDKJFKDF', 2, true,
        '4.png',
        '2021-11-29 01:23:13'),
       (5, 'sdsdsdsdsds', '123e4e639a5e0ec406270f987af7f30e', 2, 'dsdsdsdsdsdsdsd', 2, true, '5.png',
        '2021-11-29 01:24:05');

-- --------------------------------------------------------

--
-- Дамп данных таблицы auction
--
INSERT INTO auction (id, image_id, start_date, end_date, price, buyer_id, seller_id)
VALUES (1, 1, '2021-11-27 23:06:22', '2021-11-27 23:06:52', 200, 2, 1),
       (2, 3, '2021-11-29 01:23:38', '2021-11-30 01:23:38', 200, NULL, 4);

-- --------------------------------------------------------
INSERT INTO bid (id, auction_id, user_id, price, date)
VALUES (1, 1, 1, 100, '2021-11-27 23:05:44'),
       (2, 1, 2, 200, '2021-11-27 23:06:22'),
       (3, 2, 4, 100, '2021-11-29 01:21:40'),
       (4, 2, 2, 200, '2021-11-29 01:23:39');

-- --------------------------------------------------------
--
-- Дамп данных таблицы comment
--
INSERT INTO comment (id, user_id, auction_id, date, content)
VALUES (1, 4, 2, '2021-11-29 01:21:49', 'Ставки начаты'),
       (2, 4, 2, '2021-11-29 01:21:54', 'Привет всем всем привет'),
       (3, 4, 2, '2021-11-29 01:22:00', 'Привет Андрей');

-- --------------------------------------------------------
--
-- Дамп данных таблицы followers
--
INSERT INTO subscription (user_id, follower_id)
VALUES (1, 2),
       (1, 3);
-- --------------------------------------------------------

--
-- Дамп данных таблицы imagestags
--

INSERT INTO image_tag (tag_id, image_id)
VALUES (1, 3),
       (3, 3),
       (3, 4);

-- --------------------------------------------------------


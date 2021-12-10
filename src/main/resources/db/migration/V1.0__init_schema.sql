--
-- Структура таблиц
--
CREATE TABLE auction
(
    id         bigserial unique NOT NULL,
    image_id   bigint           NOT NULL,
    start_date timestamp,
    end_date   timestamp,
    price      int              NOT NULL,
    buyer_id   bigint,
    seller_id  bigint           NOT NULL
);

CREATE TABLE bid
(
    id         bigserial unique NOT NULL,
    auction_id bigint           NOT NULL,
    user_id    bigint           NOT NULL,
    price      int              NOT NULL,
    date       timestamp        NOT NULL
);

CREATE TABLE comment
(
    id         bigserial unique NOT NULL,
    user_id    bigint           NOT NULL,
    auction_id bigint           NOT NULL,
    date       timestamp        NOT NULL,
    content    text             NOT NULL
);

CREATE TABLE subscription
(
    user_id     bigint NOT NULL,
    follower_id bigint NOT NULL
);

CREATE TABLE image
(
    id           bigserial unique NOT NULL,
    title        varchar(255)     NOT NULL,
    hash         varchar(255)     NOT NULL,
    creator_id   bigint           NOT NULL,
    description  text             NOT NULL,
    owner_id     bigint           NOT NULL,
    is_activated bool             NOT NULL,
    url          varchar(512)     NOT NULL,
    upload_date  timestamp        NOT NULL
);

CREATE TABLE image_tag
(
    tag_id   bigint NOT NULL,
    image_id bigint NOT NULL
);

CREATE TABLE tag
(
    id  bigserial unique NOT NULL,
    tag varchar(64)      NOT NULL
);

CREATE TABLE "user"
(
    id         bigserial unique NOT NULL,
    login      varchar(255)     NOT NULL,
    password   varchar(255)     NOT NULL,
    about      text             NOT NULL DEFAULT '',
    points     int              NOT NULL,
    avatar_url varchar(512)     NOT NULL
);

CREATE TABLE role
(
    id   bigserial unique NOT NULL,
    name varchar(32)      NOT NULL
);

CREATE TABLE user_role
(
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);

--
-- Индексы сохранённых таблиц
--
alter table only auction
    add primary key (id);
alter table only bid
    add primary key (id);
alter table only comment
    add primary key (id);
alter table only image
    add primary key (id);
alter table only tag
    add primary key (id);
alter table only "user"
    add primary key (id);
alter table only role
    add primary key (id);
--
-- Ограничения внешнего ключа сохраненных таблиц
--
ALTER TABLE ONLY auction
    ADD CONSTRAINT auction_image_id_fk FOREIGN KEY (image_id) REFERENCES image (id);
ALTER TABLE ONLY auction
    ADD CONSTRAINT auction_buyer_id_fk FOREIGN KEY (buyer_id) REFERENCES "user" (id);
ALTER TABLE ONLY auction
    ADD CONSTRAINT auction_seller_id_fk FOREIGN KEY (seller_id) REFERENCES "user" (id);

ALTER TABLE ONLY bid
    ADD CONSTRAINT bid_auction_id_fk FOREIGN KEY (auction_id) REFERENCES auction (id);
ALTER TABLE ONLY bid
    ADD CONSTRAINT bid_user_id_fk FOREIGN KEY (user_id) REFERENCES "user" (id);

ALTER TABLE ONLY comment
    ADD CONSTRAINT comment_user_id_fk FOREIGN KEY (user_id) REFERENCES "user" (id);
ALTER TABLE ONLY comment
    ADD CONSTRAINT comment_auction_id_fk FOREIGN KEY (auction_id) REFERENCES auction (id);

ALTER TABLE ONLY subscription
    ADD CONSTRAINT subscription_user_id_fk FOREIGN KEY (user_id) REFERENCES "user" (id);
ALTER TABLE ONLY subscription
    ADD CONSTRAINT subscription_follower_id_fk FOREIGN KEY (follower_id) REFERENCES "user" (id);

ALTER TABLE ONLY image
    ADD CONSTRAINT image_creator_id_fk FOREIGN KEY (creator_id) REFERENCES "user" (id);
ALTER TABLE ONLY image
    ADD CONSTRAINT image_owner_id_fk FOREIGN KEY (owner_id) REFERENCES "user" (id);

ALTER TABLE ONLY image_tag
    ADD CONSTRAINT image_tag_tag_id_fk FOREIGN KEY (tag_id) REFERENCES tag (id) ON DELETE CASCADE;
ALTER TABLE ONLY image_tag
    ADD CONSTRAINT image_tag_image_id_fk FOREIGN KEY (image_id) REFERENCES image (id) ON DELETE CASCADE;

ALTER TABLE ONLY user_role
    ADD CONSTRAINT user_role_user_id_fk FOREIGN KEY (user_id) REFERENCES "user" (id);
ALTER TABLE ONLY user_role
    ADD CONSTRAINT user_role_role_id_fk FOREIGN KEY (role_id) REFERENCES role (id);
drop table IF EXISTS FILM_GENRE CASCADE;
drop table IF EXISTS LIKES CASCADE;
drop table IF EXISTS FRIENDS CASCADE;
drop table IF EXISTS FILM CASCADE;
drop table IF EXISTS RATING CASCADE;
drop table IF EXISTS GENRE CASCADE;
drop table IF EXISTS USERS CASCADE;

create TABLE IF NOT EXISTS GENRE (
    GENRE_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    GENRE_NAME VARCHAR(255) NOT NULL
);

create TABLE IF NOT EXISTS RATING (
    RATING_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    RATING_TITLE VARCHAR(255) NOT NULL
);

create TABLE IF NOT EXISTS FILM (
    FILM_ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    NAME VARCHAR(255) NOT NULL,
    DESCRIPTION VARCHAR(1024) NOT NULL,
    RELEASE_DATE DATE NOT NULL,
    DURATION INTEGER NOT NULL,
    RATING_ID INTEGER REFERENCES RATING(RATING_ID) NOT NULL
);

create TABLE IF NOT EXISTS USERS (
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    EMAIL VARCHAR(255) NOT NULL UNIQUE,
    LOGIN VARCHAR(255) NOT NULL UNIQUE,
    NAME VARCHAR(255),
    BIRTHDAY DATE NOT NULL
);

create TABLE IF NOT EXISTS FILM_GENRE (
    FILM_ID INTEGER NOT NULL,
    GENRE_ID INTEGER NOT NULL,
    CONSTRAINT FILM_GENRE_PK PRIMARY KEY (FILM_ID, GENRE_ID),
    CONSTRAINT FK_FILM_ID_FILM_GENRE FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID),
    CONSTRAINT FK_GENRE_ID_FILM_GENRE FOREIGN KEY (GENRE_ID) REFERENCES GENRE(GENRE_ID)
);

create TABLE IF NOT EXISTS FRIENDS (
    USER_ID INTEGER NOT NULL,
    FRIEND_ID INTEGER NOT NULL,
    CONSTRAINT FRIEND_PK PRIMARY KEY (USER_ID, FRIEND_ID),
    CONSTRAINT FK_USER_ID_FRIENDS FOREIGN KEY (USER_ID) REFERENCES USERS(ID),
    CONSTRAINT FK_FRIEND_ID_FRIENDS FOREIGN KEY (FRIEND_ID) REFERENCES USERS(ID)
);

create TABLE IF NOT EXISTS LIKES (
    FILM_ID INTEGER NOT NULL,
    USER_ID INTEGER NOT NULL,
    CONSTRAINT LIKE_PK PRIMARY KEY (FILM_ID, USER_ID),
    CONSTRAINT FK_FILM_ID_LIKES FOREIGN KEY (FILM_ID) REFERENCES FILM(FILM_ID),
    CONSTRAINT FK_USER_ID_LIKES FOREIGN KEY (USER_ID) REFERENCES USERS(ID)
);
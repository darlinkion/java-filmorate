insert into USERS ( EMAIL, LOGIN, NAME, BIRTHDAY) values
('qwer@mail.ru', 'pirog', 'ivan', '2000-04-11'),
('bestuser@yandex.ru', 'nagibator', 'alex', '1900-04-11'),
('afsafas@gmail.ru', 'Lis', 'serj', '2000-04-11'),
('fasfar@rambler.ru', 'soer', 'nikita', '1990-04-01'),
('qaffsdfa@mail.ru', 'jekastar', 'val', '2000-09-08');

merge into RATING (RATING_ID, RATING_TITLE)
VALUES
    (1, 'G'),
    (2, 'PG'),
    (3, 'PG-13'),
    (4, 'R'),
    (5, 'NC-17');

merge into GENRE (GENRE_ID, GENRE_NAME)
VALUES
    (1, 'Комедия'),
    (2, 'Драма'),
    (3, 'Мультфильм'),
    (4, 'Триллер'),
    (5, 'Документальный'),
    (6, 'Боевик');


insert into FILM ( NAME,DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) values
    ( 'pirog', 'best pirog  ever', '2000-04-11',90,1),
    ( 'all dogs', 'best dogs  ever', '2019-05-17',129,3);

insert into FILM_GENRE ( FILM_ID,GENRE_ID) values
    ( 1, 5),
    ( 2, 1);


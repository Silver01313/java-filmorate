CREATE TABLE IF NOT EXISTS users(
ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
email varchar(255) NOT null,
login varchar(255) NOT NULL,
name varchar(255),
birthday date
);

CREATE TABLE IF NOT EXISTS friendship(
user_id INTEGER REFERENCES users (id), 
friend_id INTEGER REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS film(
ID INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
name varchar(255) NOT NULL,
description varchar(255) NOT NULL,
releaseDate date NOT NULL,
duration INTEGER NOT NULL,
filmRating_id INTEGER
);

CREATE TABLE IF NOT EXISTS genre(
ID INTEGER PRIMARY KEY ,
name varchar(255)
);

CREATE TABLE IF NOT EXISTS film_genre(
film_id INTEGER REFERENCES PUBLIC.FILM(ID),
genre_id INTEGER REFERENCES PUBLIC.genre(ID)
);

CREATE TABLE IF NOT EXISTS rating(
ID INTEGER PRIMARY KEY,
name varchar(255)
);

CREATE TABLE IF NOT EXISTS film_likes(
film_id INTEGER REFERENCES film (id),
user_id INTEGER REFERENCES users (id));
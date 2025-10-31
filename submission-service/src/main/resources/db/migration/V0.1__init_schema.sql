CREATE TABLE language
(
    id       integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    language text NOT NULL
);

CREATE TABLE submission
(
    id          integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id     uuid    NOT NULL,
    problem_id  integer NOT NULL,
    language_id integer NOT NULL REFERENCES language (id),
    status      text    NOT NULL,
    created_at  timestamp without time zone NOT NULL DEFAULT now()
);

CREATE TABLE events
(
    id            integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    submission_id integer NOT NULL REFERENCES submission (id),
    status        text    NOT NULL,
    created_at    timestamp without time zone NOT NULL DEFAULT now()
);

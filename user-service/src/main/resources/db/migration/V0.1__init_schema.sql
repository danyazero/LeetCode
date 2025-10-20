CREATE TABLE users
(
    id          uuid DEFAULT gen_random_uuid() PRIMARY KEY,
    name        text NOT NULL,
    username    text NOT NULL UNIQUE,
    description text,
    avatar      text,
    password    text NOT NULL,
    created_at  timestamp without time zone NOT NULL DEFAULT now()
);

CREATE TABLE link
(
    id         integer GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    title      text NOT NULL,
    link       text NOT NULL,
    created_at timestamp without time zone NOT NULL DEFAULT now(),
    user_id    uuid NOT NULL REFERENCES users (id)
);
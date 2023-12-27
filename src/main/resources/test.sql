-- Table: public.player_props_fact

-- DROP TABLE IF EXISTS public.player_props_fact;

CREATE TABLE IF NOT EXISTS public.player_props_fact
(
    player_game_id text COLLATE pg_catalog."default" NOT NULL,
    game_id text COLLATE pg_catalog."default",
    date timestamp without time zone,
    player_id text COLLATE pg_catalog."default",
    first_name text COLLATE pg_catalog."default",
    last_name text COLLATE pg_catalog."default",
    position text COLLATE pg_catalog."default",
    team_id text COLLATE pg_catalog."default",
    abbreviation text COLLATE pg_catalog."default",
    city text COLLATE pg_catalog."default",
    conference text COLLATE pg_catalog."default",
    division text COLLATE pg_catalog."default",
    full_name text COLLATE pg_catalog."default",
    pts integer,
    rbs integer,
    asts integer,
    fgm integer,
    fga integer,
    fg_pct double precision,
    fg3m integer,
    fg3a integer,
    fg3_pct double precision,
    ftm integer,
    fta integer,
    ft_pct double precision,
    blks integer,
    stls integer,
    tos integer,
    opposing_team_full_name text COLLATE pg_catalog."default",
    opposing_team_id text COLLATE pg_catalog."default",
    postseason boolean,
    season integer,
    CONSTRAINT player_props_fact_pkey PRIMARY KEY (player_game_id),
    CONSTRAINT player_props_fact_game_id_fkey FOREIGN KEY (game_id)
        REFERENCES public.game_history_target_fact (game_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT player_props_fact_opposing_team_id_fkey FOREIGN KEY (opposing_team_id)
        REFERENCES public.team_info (team_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT player_props_fact_player_id_fkey FOREIGN KEY (player_id)
        REFERENCES public.player_info_target (player_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT player_props_fact_team_id_fkey FOREIGN KEY (team_id)
        REFERENCES public.team_info (team_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

SELECT * FROM public.player_props_fact props
LEFT JOIN public.prize_picks_projections_nba proj ON props.first_name = proj.first_name AND props.last_name = proj.last_name
WHERE props.last_name IS NOT NULL
LIMIT 10;


SELECT * FROM public.player_props_fact props
WHERE ('first_name, last_name') IN ('Jaylen', 'Brown') LIMIT 10;


SELECT * FROM PlayerPropsFactEntity WHERE (first_name, last_name) IN  AND e.pts != 0 AND e.rbs != 0 AND e.asts != 0 AND e.stls != 0 AND e.blks != 0 ORDER BY e.date DESC

SELECT * FROM player_props_fact WHERE (first_name, last_name) IN (('Jaylen', 'Brown'), ('Tyrese', 'Haliburton'), ('Tyrese', 'Maxey')) AND pts != 0 AND rbs != 0 AND asts != 0 AND stls != 0 AND blks != 0 ORDER BY date DESC LIMIT 10;

CREATE TABLE IF NOT EXISTS public.player_props_full
(
    player_game_id text,
    player_id text,
    game_id text,
    team_id text,
    date timestamp without time zone,
    pts integer,
    rbs integer,
    asts integer,
    blks integer,
    stls integer,
    tos integer,
    fg3m integer,
    fg3a integer,
    fg3_pct double precision,
    fgm integer,
    fga integer,
    fg_pct double precision,
    ftm integer,
    fta integer,
    ft_pct double precision,
    oreb integer,
    dreb integer,
    pf integer,
    min integer,
    postseason boolean,
    season integer,
    CONSTRAINT player_props_full_pkey PRIMARY KEY (player_game_id),
    CONSTRAINT player_game_history_staging_player_id_fkey FOREIGN KEY (player_id)
        REFERENCES public.player_info_target (player_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT player_game_history_staging_team_id_fkey FOREIGN KEY (team_id)
        REFERENCES public.team_info (team_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE IF NOT EXISTS public.game_history_staging_full
(
    game_id text,
    date timestamp,
    ht_id text,
    ht_score integer,
    at_id text,
    at_score integer,
    postseason boolean,
    season integer,
    CONSTRAINT game_history_staging_full_pkey PRIMARY KEY (game_id),
    CONSTRAINT game_history_staging_at_id_fkey FOREIGN KEY (at_id)
        REFERENCES public.team_info (team_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT game_history_staging_ht_id_fkey FOREIGN KEY (ht_id)
        REFERENCES public.team_info (team_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

-- Table: public.game_history_target_fact

-- DROP TABLE IF EXISTS public.game_history_target_fact;

CREATE TABLE IF NOT EXISTS public.game_history_target_fact_full
(
    game_id text,
    date timestamp,
    ht_id text,
    ht_abbreviation text,
    ht_city text,
    ht_conference text,
    ht_division text,
    ht_full_name text,
    ht_score integer,
    at_id text,
    at_abbreviation text,
    at_city text,
    at_conference text,
    at_division text,
    at_full_name text,
    at_score integer,
    postseason boolean,
    season integer,
    CONSTRAINT game_history_target_fact_full_pkey PRIMARY KEY (game_id),
    CONSTRAINT game_history_target_fact_at_id_fkey FOREIGN KEY (at_id)
        REFERENCES public.team_info (team_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT game_history_target_fact_ht_id_fkey FOREIGN KEY (ht_id)
        REFERENCES public.team_info (team_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)


-- Table: public.player_props_fact

-- DROP TABLE IF EXISTS public.player_props_fact;

CREATE TABLE IF NOT EXISTS public.player_props_fact_full
(
    player_game_id text,
    game_id text,
    date timestamp,
    player_id text,
    first_name text,
    last_name text,
    "position" text,
    team_id text,
    abbreviation text,
    city text,
    conference text,
    division text,
    full_name text,
    pts integer,
    rbs integer,
    asts integer,
    blks integer,
    stls integer,
    tos integer,
    fgm integer,
    fga integer,
    fg_pct double precision,
    fg3m integer,
    fg3a integer,
    fg3_pct double precision,
    ftm integer,
    fta integer,
    ft_pct double precision,
    oreb integer,
    dreb integer,
    pf integer,
    min integer,
    opposing_team_full_name text,
    opposing_team_id text,
    postseason boolean,
    season integer,
    CONSTRAINT player_props_fact_full_pkey PRIMARY KEY (player_game_id),
    CONSTRAINT player_props_fact_game_id_fkey FOREIGN KEY (game_id)
        REFERENCES public.game_history_target_fact (game_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT player_props_fact_opposing_team_id_fkey FOREIGN KEY (opposing_team_id)
        REFERENCES public.team_info (team_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT player_props_fact_player_id_fkey FOREIGN KEY (player_id)
        REFERENCES public.player_info_target (player_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT player_props_fact_team_id_fkey FOREIGN KEY (team_id)
        REFERENCES public.team_info (team_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE IF NOT EXISTS public.underdog_projections_nba
(
    proj_id text,
    first_name text,
    last_name text,
    stat_type text,
    line_score double precision,
    payout_multiplier double precision,
    opposing_team text,
    game_date timestamp without time zone
)

CREATE TABLE IF NOT EXISTS public.underdog_projections_nba
(
    id text,
    first_name text,
    last_name text,
    stat_type text,
    line_score text,
    payout_multiplier text,
    opposing_team text,
    game_date text
)
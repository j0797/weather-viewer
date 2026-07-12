ALTER TABLE locations
    ADD CONSTRAINT uk_locations_user_name_country_state
        UNIQUE (user_id, name, country, state);
Lancer ces commandes pour :
- créer les tables
- créer les triggers (utiles pour l'envoi de notifications)
- regarder si les données sont bien insérées
```SQL
CREATE TABLE alerte_utilisateur (
                                 id SERIAL PRIMARY KEY,
                                    user_id INTEGER,
                                    firstname VARCHAR(255),
                                    lastname VARCHAR(255),
                                    email VARCHAR(255),
                                    job VARCHAR(255),
                                    location VARCHAR(255),
                                    date TIMESTAMP
                                );

CREATE TABLE forbidden_areas (
                          id SERIAL PRIMARY KEY,
                          area VARCHAR(255)
);

-- Insert some data of your choice in the forbidden_areas table
INSERT INTO forbidden_areas (area) VALUES ('Paris');
select * from forbidden_areas;

-- Create the function that will send the notification
CREATE OR REPLACE FUNCTION notify_new_insert() RETURNS trigger AS $$
DECLARE
BEGIN
    PERFORM pg_notify('new_insert', row_to_json(NEW)::text);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Create the trigger that will call the function on insert
CREATE TRIGGER new_insert_trigger
    AFTER INSERT ON alerte_utilisateur
    FOR EACH ROW
EXECUTE FUNCTION notify_new_insert();

select * from alerte_utilisateur;
select * from forbidden_areas;

delete from alerte_utilisateur;
delete from forbidden_areas;```

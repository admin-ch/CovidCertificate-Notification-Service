DELETE
FROM notifications
WHERE true;
ALTER TABLE notifications
    DROP COLUMN IF EXISTS creation_date_time;
ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS type varchar(20) not null;
ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS start_time timestamp not null;
ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS end_time timestamp not null;
ALTER TABLE notifications
    ADD COLUMN IF NOT EXISTS is_closable boolean not null default false;
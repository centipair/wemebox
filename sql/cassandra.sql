/*usage <path>/cqlsh < src/clj/centipair/core/cql/schema.sql*/

/*Key space*/
/*Use your keyspace name*/
DROP KEYSPACE core;
CREATE KEYSPACE core WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };
USE core;

/*users*/
CREATE TABLE user_account (user_id timeuuid PRIMARY KEY, username text, email text, password text, first_name text, last_name text, active boolean);
CREATE TABLE user_profile (user_id timeuuid PRIMARY KEY, first_name text, middle_name text, last_name text, email text, website text, phone_mobile text, phone_fixed text,
       address_line1 text, street text, city text, state text, country text, profile_photo text, 
       gender text, birth_date text, birth_year int, chat_channel text, chat_id text);
CREATE TABLE user_login_username(username text PRIMARY KEY, user_id timeuuid);
CREATE TABLE user_login_email(email text PRIMARY KEY, user_id timeuuid);
CREATE TABLE user_session (auth_token text PRIMARY KEY, session_expire_time timestamp, user_id timeuuid);
CREATE TABLE user_session_index (user_id timeuuid, auth_token text, PRIMARY KEY(user_id, auth_token));
CREATE TABLE user_account_registration(registration_key timeuuid PRIMARY KEY, user_id timeuuid);
CREATE TABLE password_reset(password_reset_key timeuuid PRIMARY KEY, user_id timeuuid, expiry timestamp);

CREATE TABLE box(box_id timeuuid PRIMARY KEY, box_name text);
CREATE TABLE domain_box(domain_name text PRIMARY KEY, box_id timeuuid);
CREATE TABLE box_page(box_page text PRIMARY KEY, box_id timeuuid);
CREATE TABLE box_admin(user_id timeuuid PRIMARY KEY, box_id timeuuid);
CREATE TABLE page(page_id timeuuid PRIMARY KEY, title text, content text, last_edited timestamp);
CREATE TABLE url(box_id timeuuid, url text, page_id timeuuid, PRIMARY KEY(box_id, url));
CREATE TABLE tag(tag_name text PRIMARY KEY, box_id_id timeuuid, url text);

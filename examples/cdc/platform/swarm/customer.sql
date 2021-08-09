drop table if exists source_customer;

CREATE TABLE source_customer
(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name character varying(255),
    email character varying(255)
);

drop table if exists target_customer;

CREATE TABLE target_customer
(
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name character varying(255),
    email character varying(255)
);

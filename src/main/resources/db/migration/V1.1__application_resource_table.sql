-- Table: application_resource

-- DROP TABLE application_resource;

CREATE TABLE application_resource
(
  key character varying(20),
  value bytea,
  CONSTRAINT application_resource_key_pkey PRIMARY KEY (key)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE application_resource
  OWNER TO ${dbuser};
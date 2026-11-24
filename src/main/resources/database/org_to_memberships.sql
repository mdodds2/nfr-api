CREATE TABLE org_memberships (
  id BINARY(16) PRIMARY KEY,
  org_id BINARY(16) NOT NULL REFERENCES organizations(id),
  user_id BINARY(16) NOT NULL REFERENCES users(id),
  org_role VARCHAR(20) NOT NULL,          -- ORG_ADMIN, MEMBER
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE (org_id, user_id)
);

ALTER TABLE reports ADD org_id BINARY(16) NULL REFERENCES organizations(id);
ALTER TABLE category ADD org_id BINARY(16) NULL;  -- null means global catalog
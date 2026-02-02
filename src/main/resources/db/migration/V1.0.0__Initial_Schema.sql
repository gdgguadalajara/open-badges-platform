create table Account (
    id blob not null,
    createdAt bigint not null,
    email varchar(255) unique,
    fullName varchar(255) not null,
    isSuperAdmin boolean not null,
    primary key (id)
);

create table Assertion (
    id blob not null,
    evidence varchar(255),
    htmlPayload TEXT,
    isPublic boolean not null,
    isRevoked boolean not null,
    issuedOn bigint not null,
    jsonPayload TEXT,
    recipientEmail varchar(255) not null,
    revocationReason varchar(255),
    account_id blob,
    badge_class_id blob not null,
    primary key (id)
);

create table BadgeClass (
    id blob not null,
    createdAt bigint not null,
    criteriaMd TEXT,
    description TEXT not null,
    jsonPayload TEXT,
    name varchar(255) not null,
    image_id blob,
    issuer_id blob not null,
    primary key (id)
);

create table Image (
    id blob not null,
    content_type varchar(255) not null,
    createdAt bigint not null,
    data BLOB not null,
    primary key (id)
);

create table Issuer (
    id blob not null,
    createdAt bigint not null,
    description varchar(255) not null,
    email varchar(255) not null,
    jsonPayload clob,
    logoUrl varchar(255) not null,
    name varchar(255) not null,
    url varchar(255) not null,
    primary key (id)
);

create table IssuerMember (
    id blob not null,
    createdAt bigint not null,
    role varchar(255) not null check ((role in ('OWNER','ADMIN'))),
    account_id blob not null,
    issuer_id blob not null,
    primary key (id)
);

create table LinkedEmail (
    id blob not null,
    email varchar(255) not null,
    expiresAt bigint not null,
    verificationCode varchar(255),
    verified boolean,
    account_id blob not null,
    primary key (id)
);

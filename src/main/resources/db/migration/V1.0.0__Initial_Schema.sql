create table Account (
    isSuperAdmin boolean not null,
    createdAt timestamp(6) with time zone not null,
    id uuid not null,
    email varchar(255) unique,
    fullName varchar(255) not null,
    primary key (id)
);

create table Assertion (
    isPublic boolean not null,
    isRevoked boolean not null,
    issuedOn timestamp(6) with time zone not null,
    account_id uuid,
    badge_class_id uuid not null,
    id uuid not null,
    evidence varchar(255),
    htmlPayload TEXT,
    jsonPayload TEXT,
    recipientEmail varchar(255) not null,
    revocationReason varchar(255),
    primary key (id)
);

create table BadgeClass (
    createdAt timestamp(6) with time zone not null,
    id uuid not null,
    image_id uuid,
    issuer_id uuid not null,
    criteriaMd TEXT,
    description TEXT not null,
    jsonPayload TEXT,
    name varchar(255) not null,
    primary key (id)
);

create table Image (
    createdAt timestamp(6) with time zone not null,
    id uuid not null,
    content_type varchar(255) not null,
    data bytea not null,
    primary key (id)
);

create table Issuer (
    createdAt timestamp(6) with time zone not null,
    id uuid not null,
    description TEXT not null,
    email varchar(255) not null,
    jsonPayload TEXT,
    logoUrl varchar(255) not null,
    name varchar(255) not null,
    url varchar(255) not null,
    primary key (id)
);

create table IssuerMember (
    createdAt timestamp(6) with time zone not null,
    account_id uuid not null,
    id uuid not null,
    issuer_id uuid not null,
    role varchar(255) not null check ((role in ('OWNER','ADMIN'))),
    primary key (id),
    constraint uc_issuer_account unique (issuer_id, account_id)
);

create table LinkedEmail (
    verified boolean,
    expiresAt timestamp(6) with time zone not null,
    account_id uuid not null,
    id uuid not null,
    email varchar(255) not null,
    verificationCode varchar(255),
    primary key (id)
);

alter table if exists Assertion 
    add constraint FKnv0hwl3p56f65n3os94ycak9 
    foreign key (account_id) 
    references Account;

alter table if exists Assertion 
    add constraint FKl86oees7l6rth6s2ou7iffmia 
    foreign key (badge_class_id) 
    references BadgeClass;

alter table if exists BadgeClass 
    add constraint FKd71s39n1pxg0p59r6v8n7n2o5 
    foreign key (image_id) 
    references Image;

alter table if exists BadgeClass 
    add constraint FK3kcqbac2fwb90b946q140q0l9 
    foreign key (issuer_id) 
    references Issuer;

alter table if exists IssuerMember 
    add constraint FK69hwfax8jslspd1a2kyx7ic24 
    foreign key (account_id) 
    references Account;

alter table if exists IssuerMember 
    add constraint FK35ahik3j72fatu53a75ewbhnd 
    foreign key (issuer_id) 
    references Issuer;

alter table if exists LinkedEmail 
    add constraint FKqb4ofn2tlc11fpkfsr28sct49 
    foreign key (account_id) 
    references Account;

/**
 * Manual service for assertion revocation.
 * Consumes the endpoints defined in SPECS/revoke-badges.md.
 * To be replaced by orval-generated code once the backend implements the endpoints.
 */
import type { Uuid } from '../../models';

export interface RevokeAssertionRequest {
  reason: string;
}

export interface RevokeAssertionResponse {
  assertionId?: Uuid;
  isRevoked?: boolean;
  revocationReason?: string;
  revokedAt?: string;
}

export interface UnrevokeAssertionResponse {
  assertionId?: Uuid;
  isRevoked?: boolean;
}

export interface RevokeBadgeAssertionsRequest {
  reason: string;
  assertionIds?: Uuid[];
}

export interface RevokeBadgeAssertionsResponse {
  issuerId?: Uuid;
  badgeClassId?: Uuid;
  reason?: string;
  total?: number;
  revoked?: number;
  skipped?: number;
  revokedAt?: string;
  assertionIds?: Uuid[];
}

const patch = async (url: string, body: unknown, options?: RequestInit) => {
  const res = await fetch(url, {
    ...options,
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json', ...options?.headers },
    body: JSON.stringify(body),
  });
  const text = await res.text();
  const data = text ? JSON.parse(text) : {};
  return { data, status: res.status };
};

export const getPatchApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertionsAssertionUuidRevokeUrl = (
  issuerUuid: Uuid,
  badgeClassUuid: Uuid,
  assertionUuid: Uuid,
) => `/api/v2/issuers/${issuerUuid}/badges/${badgeClassUuid}/assertions/${assertionUuid}/revoke`;

export const patchApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertionsAssertionUuidRevoke = async (
  issuerUuid: Uuid,
  badgeClassUuid: Uuid,
  assertionUuid: Uuid,
  body: RevokeAssertionRequest,
  options?: RequestInit,
): Promise<{ data: RevokeAssertionResponse; status: number }> =>
  patch(getPatchApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertionsAssertionUuidRevokeUrl(issuerUuid, badgeClassUuid, assertionUuid), body, options);

export const getPatchApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertionsAssertionUuidUnrevokeUrl = (
  issuerUuid: Uuid,
  badgeClassUuid: Uuid,
  assertionUuid: Uuid,
) => `/api/v2/issuers/${issuerUuid}/badges/${badgeClassUuid}/assertions/${assertionUuid}/unrevoke`;

export const patchApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertionsAssertionUuidUnrevoke = async (
  issuerUuid: Uuid,
  badgeClassUuid: Uuid,
  assertionUuid: Uuid,
  options?: RequestInit,
): Promise<{ data: UnrevokeAssertionResponse; status: number }> =>
  patch(getPatchApiV2IssuersIssuerUuidBadgesBadgeClassUuidAssertionsAssertionUuidUnrevokeUrl(issuerUuid, badgeClassUuid, assertionUuid), {}, options);

export const getPatchApiV2IssuersIssuerUuidBadgesBadgeClassUuidRevokeUrl = (
  issuerUuid: Uuid,
  badgeClassUuid: Uuid,
) => `/api/v2/issuers/${issuerUuid}/badges/${badgeClassUuid}/revoke`;

export const patchApiV2IssuersIssuerUuidBadgesBadgeClassUuidRevoke = async (
  issuerUuid: Uuid,
  badgeClassUuid: Uuid,
  body: RevokeBadgeAssertionsRequest,
  options?: RequestInit,
): Promise<{ data: RevokeBadgeAssertionsResponse; status: number }> =>
  patch(getPatchApiV2IssuersIssuerUuidBadgesBadgeClassUuidRevokeUrl(issuerUuid, badgeClassUuid), body, options);
package org.acmebank.people.domain;

public record EvidenceRating(Score score, String rationale) {
    public EvidenceRating {
        if (score == null) throw new IllegalArgumentException("Score cannot be null");
        if (rationale == null) rationale = "";
    }
}

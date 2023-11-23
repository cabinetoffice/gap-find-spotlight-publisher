package gov.cabinetoffice.gap.spotlight.publisher.enums;


public enum GrantApplicantOrganisationType {

    LIMITED_COMPANY("Limited company"), NON_LIMITED_COMPANY("Non-limited company"),
    UNLIMITED_COMPANY("Unlimited company"), REGISTERED_CHARITY("Registered charity"),
    UNREGISTERED_CHARITY("Unregistered charity"), OTHER("Other"), CHARITY("Charity"),
    INDIVIDUAL("I am applying as an Individual");

    private final String name;

    GrantApplicantOrganisationType(String name) {
        this.name = name;
    }

    public static GrantApplicantOrganisationType valueOfName(String name) {
        for (GrantApplicantOrganisationType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }


}

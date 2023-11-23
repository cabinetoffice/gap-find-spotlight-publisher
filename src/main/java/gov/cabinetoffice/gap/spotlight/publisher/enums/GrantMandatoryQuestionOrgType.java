package gov.cabinetoffice.gap.spotlight.publisher.enums;

public enum GrantMandatoryQuestionOrgType {

    LIMITED_COMPANY("Limited company"), NON_LIMITED_COMPANY("Non-limited company"),
    REGISTERED_CHARITY("Registered charity"), UNREGISTERED_CHARITY("Unregistered charity"), CHARITY("Charity"),
    INDIVIDUAL("I am applying as an Individual"), OTHER("Other");

    private final String name;

    GrantMandatoryQuestionOrgType(String name) {
        this.name = name;
    }

    public static GrantMandatoryQuestionOrgType valueOfName(String name) {
        for (GrantMandatoryQuestionOrgType type : values()) {
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

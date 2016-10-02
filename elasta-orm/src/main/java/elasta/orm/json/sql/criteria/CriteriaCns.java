package elasta.orm.json.sql.criteria;

/**
 * Created by Jango on 9/16/2016.
 */
public enum CriteriaCns {
    EQ("$eq"), GT("$gt"), GTE("$gte"), LT("$lt"), LTE("$lte"), NE("$ne"),
    EQI("$eqi"), LK("$lk"), NLK("$nlk"), STW("$stw"), ENDW("$endw"), CNTNS("$cntns"),
    IN("$in"), NIN("$nin");
    private final String value;

    CriteriaCns(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}

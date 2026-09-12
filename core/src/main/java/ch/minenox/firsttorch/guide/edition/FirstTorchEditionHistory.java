package ch.minenox.firsttorch.guide.edition;

import java.util.ArrayList;
import java.util.List;
import static ch.minenox.firsttorch.guide.edition.EditionHistory.Kind.*;

/** Reviewed semantic history from docs/EditionDifferences.md; cosmetic edits are not events. */
public final class FirstTorchEditionHistory {
    private FirstTorchEditionHistory() {}
    public static EditionHistory create() {
        var changes = new ArrayList<EditionHistory.Change>();
        changes.add(new EditionHistory.Change("1CA0B0C0D0E00003", "26.1.2", NEW));
        for (var id : List.of("4A28C6E10D735BF9", "6C4AE8F31D957B20", "18A6D3F90C754BE2",
                "59CBED086F24A137", "38D24F61E9DA3570", "4BBE2FCA33A44E31"))
            changes.add(new EditionHistory.Change(id, "26.1.2", REVISED));
        for (var id : List.of("15E7C9A42B806DF3", "12E8A5C74F309BD6",
                "1A62D8E30C745BF9", "49CBF6082DA57E13", "34FAC7E96152BDF8"))
            changes.add(new EditionHistory.Change(id, "26.1.2", CONTEXT));
        for (var id : List.of("2A26200000000001", "2A26200000000002", "2A26200000000003",
                "2A26200000000004", "2A26200000000005"))
            changes.add(new EditionHistory.Change(id, "26.2", NEW));
        changes.add(new EditionHistory.Change("32B4C6D8E0F21357", "26.2", REVISED));
        return new EditionHistory(List.of("1.21.1", "26.1.2", "26.2"), changes);
    }
}

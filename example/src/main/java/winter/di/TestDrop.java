package winter.di;

import winter.di.annotations.Snowdrop;
import winter.di.tt.TestTTDrop;

@Snowdrop
public class TestDrop {
    private final TestTTDrop drop;

    public TestDrop(TestTTDrop drop) {
        this.drop = drop;
    }
}

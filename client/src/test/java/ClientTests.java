import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import sirs.remotedocs.ClientApp;
import sirs.remotedocs.LoginRegisterForm;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ClientTests {
    private ClientApp clientApp = new ClientApp("localhost", 8080);

    @BeforeAll
    void setUp() {
        this.clientApp.switchForm(this.clientApp.getMenu(), this.clientApp.getRegister());
    }

}

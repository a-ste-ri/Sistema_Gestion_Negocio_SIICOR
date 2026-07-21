
import ui.MenuPrincipalView;

public class Main {
    public static void main(String[] args) {
        
        database.CrearTablas.crear();
        new MenuPrincipalView().setVisible(true);
    }
}
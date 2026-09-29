package mcgill.ecse420.a1;

import java.util.concurrent.Executor;

public class MulitplierThread implements Executor {
    public void execute(Runnable r) {
        r.run();
    }
    
}

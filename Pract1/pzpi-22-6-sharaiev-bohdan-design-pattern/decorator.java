import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

interface IRadiation {
    double getRadiationLevel();
}

class Radiation implements IRadiation {
    private final double level;

    public Radiation(double level) {
        this.level = level;
    }

    @Override
    public double getRadiationLevel() {
        return level;
    }
}

abstract class RadiationDecorator implements IRadiation {
    protected IRadiation baseData;

    public RadiationDecorator(IRadiation baseData) {
        this.baseData = baseData;
    }

    @Override
    public double getRadiationLevel() {
        return baseData.getRadiationLevel();
    }
}

class LogRadiationDecorator extends RadiationDecorator {

    public LogRadiationDecorator(IRadiation baseData) {
        super(baseData);
    }

    @Override
    public double getRadiationLevel() {
        double level = baseData.getRadiationLevel();
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String formattedTime = now.format(formatter);
        System.out.println("Radiation level at " + formattedTime + ": " + level);
        return level;
    }
}

public class Main {
    public static void main(String[] args) {
        IRadiation data = new Radiation(3.45);

        IRadiation loggedData = new LogRadiationDecorator(data);

        System.out.println("Final radiation level: " + loggedData.getRadiationLevel());
    }
}

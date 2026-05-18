package pt.ipp.estg.pp.core;

import java.time.LocalDateTime;

public interface Measurement {
   LocalDateTime getDate();

   double getValue();
}

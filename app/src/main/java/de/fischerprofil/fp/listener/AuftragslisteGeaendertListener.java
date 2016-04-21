package de.fischerprofil.fp.listener;

import java.util.EventListener;

public interface AuftragslisteGeaendertListener extends EventListener{
    public  void onAuftragslisteGeaendert(String l);
}

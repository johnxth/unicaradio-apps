# Permessi utilizzati dall'app UnicaRadio Android™ #

Quando si scarica una applicazione dal Market Android™, viene mostrato un elenco di permessi che l'utente deve accettare di concedere all'applicazione per poterla utilizzare.

Anche l'applicazione UnicaRadio utilizza alcune autorizzazioni che l'utente deve accettare. I permessi utilizzati sono i seguenti:

| **Permesso** | **Significato** | **Motivo dell'utilizzo** |
|:-------------|:----------------|:-------------------------|
| INTERNET     | Accesso ad Internet | Ascolto musica, invio richieste, download palinsesto |
| ACCESS\_NETWORK\_STATE | Accesso allo stato della rete | Controllo dello stato della connessione 3G |
| ACCESS\_WIFI\_STATE | Accesso allo stato della rete Wi-Fi | Controllo dello stato della rete Wi-Fi |
| READ\_PHONE\_STATE | Controllo sullo stato del telefono | Disattivazione riproduzione all'arrivo di una telefonata |
| GET\_ACCOUNTS | Accesso agli account | Gli account e-mail Google vengono mostrati come suggerimento nel campo e-mail per l'invio di richieste |
| WAKE\_LOCK   | Disattivazione stand-by | Utilizzato dalle librerie [GCM](http://developer.android.com/google/gcm/index.html) per la ricezione di messaggi dalla redazione |
| VIBRATE      | Vibrazione      | All'arrivo di un nuovo messaggio il dispositivo vibra |

Per maggiori dettagli sui permessi Android™ consulta la [documentazione](http://developer.android.com/reference/android/Manifest.permission.html).
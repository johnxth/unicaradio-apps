# Preparazione Ambiente per sviluppo su Android™ #

## Preparazione al download ##

  * Aggiungere al file ~/.netrc la seguente linea (creare il file se non esiste)
```
machine code.google.com login GOOGLECODE_USER password GOOGLECODE_PASSWORD
```
Sostituire GOOGLECODE\_USER e GOOGLECODE\_PASSWORD con username e password di Google Code

## Scaricare il progetto ##
```
git clone https://code.google.com/p/unicaradio-apps/
```

## Preparazione ambiente ##

### Prerequisiti ###

  * JDK 6 Sun
  * Eclipse 3.6 INDIGO (versione Java EE)

### Installazione Android™ SDK (Linux) ###

  * Eseguire (aggiornare la revisione dell'SDK se necessario (vedi http://developer.android.com/sdk/index.html)
```
mkdir -pv ~/bin/
cd /tmp
wget http://dl.google.com/android/android-sdk_r20-linux.tgz
cd ~/bin
tar xvf /tmp/android-sdk_r20-linux.tgz
```

  * aggiungi nel file ~/.bash\_profile le righe
```
export ANDROID_SDK="~/android-sdk-linux" 
export PATH=${PATH}:${ANDROID_SDK}/tools:${ANDROID_SDK}/platform-tools
alias sqlite3="/usr/bin/sqlite3" 
```


### Preparazione Eclipse ###

  * Su Eclipse, cliccare sul Menu **Help**, poi su **Install New Software**
  * Nella casella "Work with" inserire **http://dl-ssl.google.com/android/eclipse**
  * Comparira il plugin Developer Tools, selezionarlo assieme ai "figli" e cliccare Next
  * quando l'installazione sarà terminata, riavviare Eclipse
  * all'avvio successivo verrà mostrata una dialog di configurazione della SDK, devi scegliere "use existing SDK", e indicare ~/bin/android-sdk-linux

### Installazione API ed emulatori ###

  * Da Eclipse, aprire Window > Android SDK Manager
  * Attendere il download delle informazioni dai server google. Installare:
  * Android SDK Tools
  * Android SDK Platform-tools
  * Per ogni versione di android installare
    * SDK Platform
    * Google APIs
    * Eventualmente installare la documentazione e gli esempi
  * In Extras installare tutto il disponibile.
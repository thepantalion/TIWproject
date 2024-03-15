### Project description
This project proposed the creation of both the back-end (in Java) and the front-end (HTML) for a website that manages meetings and participants to those meetings. The specifications are reported below (in italian, an english version will be added later).


Un'applicazione web consente la gestione di riunioni online. 
L'applicazione supporta registrazione e login mediante una pagina pubblica con opportune form. La registrazione controlla la validità sintattica dell'indirizzo di email e l'uguaglianza tra i campi "password" e
"ripeti password". 
La registrazione controlla l'unicità dello username. Una riunione ha un titolo, una data, un'ora, una durata e un numero massimo di partecipanti. 
L'utente fa il login e, se autenticato, accede all'**HOME** page che mostra l'elenco delle riunioni indette da lui e non ancora scadute, l'elenco delle riunioni cui è stato invitato e non ancora scadute, e una form per creare una nuova riunione. 
Quando l'utente inoltra la form con il bottone _INVIA_, appare una pagina **ANAGRAFICA** con l'elenco degli utenti registrati. L'utente può scegliere uno o più partecipanti dall'elenco e premere il bottone _INVITA_ per invitarli alla riunione. 
Se il numero d'invitati è superiore di X unità rispetto al massimo ammissibile, appare di nuovo la pagina **ANAGRAFICA** con un messaggio "Troppi utenti selezionati, eliminarne almeno X". 
La pagina evidenzia nell'elenco gli utenti scelti in precedenza come preselezionati, in modo che l'utente possa deselezionarne alcuni. Se alla pressione del bottone _INVITA_ il numero d'invitati è inferiore al massimo ammissibile, 
la riunione è memorizzata nella base di dati e associata agli utenti invitati e l'utente è rimandato alla **HOME PAGE**. Al terzo tentativo scorretto di assegnare troppi invitati a una riunione appare una pagina **CANCELLAZIONE** con un messaggio 
"Tre tentativi di definire una riunione con troppi partecipanti, la riunione non sarà creata" e un link per tornare all'**HOME PAGE**. In questo caso la riunione NON è memorizzata nella base di dati.
L'applicazione non deve registrare nella base di dati riunioni con numero eccessivo di partecipanti. L'applicazione consente il logout dell'utente.

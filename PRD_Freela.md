# Freela — Product Requirements Document

**Versione:** 1.0 (MVP)
**Autori:** Mattia & Riccardo
**Corso:** Laboratorio di Programmazione per Sistemi Mobile e Tablet, A.A. 2025/2026
**Stato:** Draft pre-implementazione

---

## 1. Executive Summary

Freela è un CRM mobile-first pensato per il singolo freelancer. A differenza dei CRM aziendali (HubSpot, Salesforce) o delle piattaforme di business management (HoneyBook, Bonsai), Freela è costruito attorno alla giornata reale di un professionista autonomo: pochi clienti, alta intensità relazionale, lavoro frammentato fra chiamate, messaggi e spostamenti, telefono come strumento principale.

Il problema che risolve non è la mancanza di strumenti — il freelancer ne usa già troppi (WhatsApp, email, calendario, fogli Excel, note sparse). Il problema è la **frammentazione**, che porta a follow-up dimenticati, scadenze mancate, ore non tracciate e pagamenti in ritardo.

La V1 (MVP) copre l'intero ciclo di vita del cliente: lead → contatto → preventivo → incarico → lavoro → consegna → pagamento → fidelizzazione. L'app è sviluppata in **Kotlin con Jetpack Compose**, persiste i dati localmente con **Room (SQLite)**, ed è progettata con un'architettura a layer (Repository pattern) che rende possibile aggiungere un backend remoto in V2 senza riscrivere la UI o la logica di dominio.

### Obiettivi del progetto

- Consegnare un MVP funzionante per la valutazione finale del corso.
- Dimostrare padronanza dei componenti Android trattati a lezione: Activity/Compose, persistenza (SQLite/Room + SharedPreferences), thread/coroutines, services, broadcasts, notifiche, sensori (localizzazione GPS).
- Costruire una base di codice estendibile, in cui l'aggiunta di un backend remoto sia un'evoluzione naturale e non una riscrittura.

---

## 2. Problema, opportunità e obiettivi

### 2.1 Il problema reale

Un freelancer lavora tipicamente da solo e deve gestire contemporaneamente cinque attività che, in azienda, sarebbero divise fra più ruoli: commerciale (lead, preventivi), project management (consegne, scadenze), amministrazione (fatture, pagamenti), relazione cliente (chat, email, call) e organizzazione personale (tempo, priorità).

Il risultato è che le informazioni di ogni cliente vivono sparse fra cinque-sette strumenti diversi. Una richiesta arriva su Instagram, viene continuata via WhatsApp, il preventivo si scrive in Word, le scadenze sono sul calendario di Google, le ore lavorate su un foglio Excel, e i pagamenti vengono ricordati a memoria. Niente di tutto questo parla con il resto.

### 2.2 Conseguenze concrete della frammentazione

- Lead dimenticati = potenziali clienti persi
- Follow-up mancati = trattative che si raffreddano e non si chiudono
- Ore non tracciate = ricavi che il freelancer non sa di star perdendo
- Scadenze sparse fra strumenti = immagine professionale peggiore
- Pagamenti in ritardo non monitorati = stress economico e instabilità di cassa

### 2.3 Perché esiste un'opportunità

Il mercato dei CRM è maturo, ma le soluzioni esistenti hanno un baricentro diverso. **HoneyBook** è una piattaforma all-in-one per piccoli business indipendenti, ricca e completa ma proprio per questo pesante. **HubSpot** è potentissimo ma pensato per team commerciali e flussi B2B strutturati. **Bigin (Zoho)** è il più vicino concettualmente, ma resta un CRM small business generalista. **Bonsai** è centrato su tempo e fatturazione più che sulla relazione.

Nessuno di questi prodotti è stato progettato esplicitamente attorno alla giornata mobile di un freelancer singolo, ed è qui che Freela trova il suo spazio.

### 2.4 Obiettivi misurabili (MVP)

Vista la natura del progetto (universitario, due sviluppatori, scope contenuto), gli obiettivi sono espressi come criteri di qualità verificabili sul prodotto, non come KPI di business.

- **Copertura funzionale**: tutte e 7 le funzioni V1 implementate e usabili end-to-end.
- **Stabilità**: nessun crash sui flussi principali (creazione cliente, pipeline, time tracking, notifica reminder).
- **Offline-first**: l'app è pienamente funzionante senza connessione di rete.
- **Coerenza didattica**: ogni componente del syllabus del corso (Activity/Compose, persistenza, thread/services, broadcasts, notifiche, sensori) è esercitato dal prodotto in modo significativo, non posticcio.
- **Manutenibilità**: l'architettura permette di sostituire il data layer locale con un'API remota modificando solo l'implementazione del Repository.

---

## 3. Personas

Tre personas guidano le decisioni di prodotto. Sono profili compositi basati sui segmenti target (designer, social media manager, fotografi, sviluppatori, copywriter, consulenti).

### 3.1 Giulia, 27 anni — Social Media Manager freelance

- Gestisce 8 clienti attivi, mix di piccole aziende locali e content creator.
- Le richieste arrivano da Instagram DM, email e passaparola. Risponde quasi sempre dal telefono.
- **Frustrazione principale**: dimentica i follow-up sui preventivi e perde 2-3 lead al mese per pura disorganizzazione.
- **Cosa si aspetta da Freela**: una vista "oggi devo" che le dica chi richiamare, quali materiali aspetta, e a chi inviare un sollecito gentile sui pagamenti.

### 3.2 Luca, 31 anni — Web designer freelance

- Lavora a progetto, mediamente 4-6 clienti in parallelo. Ogni progetto dura 3-8 settimane.
- **Frustrazione principale**: sottostima costantemente il tempo necessario. Si rende conto a fine progetto di aver lavorato il doppio delle ore preventivate.
- **Cosa si aspetta da Freela**: time tracking semplice, confronto fra ore preventivate e ore reali, e abbastanza dati per fare preventivi futuri più accurati.

### 3.3 Sara, 29 anni — Fotografa freelance

- Lavora molto in mobilità (matrimoni, eventi, shooting in location). Vive con il telefono in mano.
- Ha relazioni personali intense con i clienti: ricorda i nomi dei figli, gli anniversari, le preferenze.
- **Frustrazione principale**: dopo un anno non riesce più a ricostruire "chi era chi" e cosa è successo con un cliente, perché tutto è sparso fra WhatsApp e note vocali.
- **Cosa si aspetta da Freela**: uno storico ordinato per cliente, la possibilità di taggare luoghi e date degli incontri, e un riepilogo annuale che le dica quali clienti contano di più.

---

## 4. Scope della V1

### 4.1 Cosa entra in V1 (MVP)

La V1 implementa sette pilastri funzionali, tutti integrati in un'unica esperienza coerente.

1. **Gestione clienti e contatti**: scheda unificata con dati anagrafici, note, file allegati, storico interazioni.
2. **Pipeline visuale**: stato della trattativa lungo dieci fasi predefinite (da "Nuovo lead" a "Cliente ricorrente").
3. **Task e reminder intelligenti**: azioni manuali e suggerimenti automatici basati sullo stato dei record (es. "preventivo inviato 5 giorni fa").
4. **Note e storico delle interazioni**: timeline cronologica di tutto quello che è successo con un cliente.
5. **Dashboard giornaliera ("Oggi")**: cosa contattare, cosa consegnare, chi deve pagare.
6. **Time tracking leggero**: timer per progetto, inserimento manuale, confronto preventivo vs reale.
7. **Mini-finanza operativa**: stato dei preventivi, fatture emesse, pagamenti ricevuti e in ritardo, entrate previste del mese.

### 4.2 Cosa NON entra in V1 (anti-requisiti espliciti)

La scelta esplicita di cosa NON fare è importante quanto la scelta di cosa fare. Freela V1:

- **Non è** un software di contabilità o fatturazione fiscale (no XML SDI, no integrazione AdE).
- **Non è** una piattaforma di team collaboration: è single-user per design.
- **Non sostituisce** WhatsApp o email: non gestisce la messaggistica reale, solo traccia le interazioni.
- **Non gestisce** firma di contratti né documenti legali.
- **Non ha** ruoli o permessi multi-utente.
- **Non ha** sincronizzazione cloud in V1 (rinviata a V2; l'architettura però è predisposta).

### 4.3 Cosa è rinviato (visione V2 e V3)

- **V2**: backend remoto con sync multi-device, preventivi smart con template, integrazione calendario, notifiche più contestuali.
- **V3**: AI assistant per riassunti delle interazioni, generazione bozza preventivo da brief, predizione clienti a rischio inattività.

---

## 5. User Stories

Formato standard "Come [ruolo] voglio [azione] per [beneficio]". Ogni storia ha un codice (US-XX) referenziato nei requisiti funzionali.

### 5.1 Gestione clienti

- **US-01** — Come freelancer voglio creare un nuovo cliente con i dati essenziali (nome, contatto, fonte di acquisizione) in meno di 30 secondi, per poter registrare un lead al volo dopo una conversazione.
- **US-02** — Come freelancer voglio vedere una scheda unificata di ogni cliente con tutte le informazioni (anagrafica, note, storico, file, prossima azione), per avere il contesto completo in un solo punto.
- **US-03** — Come freelancer voglio cercare un cliente per nome o per tag, per ritrovarlo rapidamente quando mi richiama dopo mesi.
- **US-04** — Come freelancer voglio allegare file a un cliente (brief PDF, immagini di riferimento), per non doverli cercare in chat ogni volta.

### 5.2 Pipeline

- **US-05** — Come freelancer voglio vedere tutti i miei clienti raggruppati per fase della pipeline, per capire a colpo d'occhio dove si concentrano le mie energie.
- **US-06** — Come freelancer voglio spostare un cliente da una fase all'altra (es. da "Preventivo inviato" a "In trattativa"), per aggiornare lo stato senza compilare form.
- **US-07** — Come freelancer voglio filtrare la pipeline per fase o per data, per concentrarmi solo sui lead caldi della settimana.

### 5.3 Task e reminder

- **US-08** — Come freelancer voglio creare un task collegato a un cliente con scadenza e ora, per ricordarmi di richiamarlo.
- **US-09** — Come freelancer voglio ricevere una notifica al momento giusto per i task con scadenza, anche se l'app è chiusa o il telefono è stato riavviato.
- **US-10** — Come freelancer voglio che l'app mi suggerisca proattivamente azioni di follow-up (es. "hai inviato un preventivo 5 giorni fa, vuoi mandare un sollecito?"), per non perdere opportunità per pigrizia o dimenticanza.

### 5.4 Note e storico interazioni

- **US-11** — Come freelancer voglio aggiungere note libere a un cliente, per appuntare cose dette in chiamata o impressioni personali.
- **US-12** — Come freelancer voglio registrare un'interazione (call, meeting, messaggio) con data, tipo e breve descrizione, per costruire uno storico cronologico.
- **US-13** — Come freelancer voglio taggare il luogo di un meeting con la mia posizione GPS, per ricordare dove ho incontrato un cliente (utile per fotografi, consulenti, agenti).

### 5.5 Dashboard giornaliera

- **US-14** — Come freelancer voglio una schermata "Oggi" che mi dica chi devo contattare, cosa devo consegnare e chi deve pagarmi, per iniziare la giornata sapendo dove mettere le mani.
- **US-15** — Come freelancer voglio vedere uno "storico" della settimana o del mese con il riepilogo di come ho distribuito l'attenzione fra i clienti, per capire quali sto trascurando e quali mi stanno mangiando troppo tempo.

### 5.6 Time tracking

- **US-16** — Come freelancer voglio avviare un timer associato a un progetto con un tap, per registrare le ore mentre lavoro.
- **US-17** — Come freelancer voglio aggiungere ore manualmente a posteriori, perché non sempre ricordo di far partire il timer.
- **US-18** — Come freelancer voglio confrontare le ore preventivate con quelle effettivamente lavorate per ogni cliente, per capire la redditività reale.

### 5.7 Mini-finanza

- **US-19** — Come freelancer voglio registrare un preventivo (importo, data, stato), per tenere traccia di cosa ho proposto.
- **US-20** — Come freelancer voglio registrare una fattura emessa con data scadenza, per sapere quando aspettarmi il pagamento.
- **US-21** — Come freelancer voglio segnare una fattura come pagata, per chiudere il ciclo del cliente.
- **US-22** — Come freelancer voglio vedere il totale dei pagamenti attesi nel mese e quelli in ritardo, per gestire la mia liquidità.

---

## 6. Requisiti funzionali (FR)

### 6.1 Modulo Clienti

- **FR-01** — L'app permette di creare, modificare ed eliminare clienti. Campi obbligatori: nome. Campi opzionali: telefono, email, fonte di acquisizione, tag, note, foto profilo. *[US-01]*
- **FR-02** — Ogni cliente ha una scheda dettaglio che mostra: dati anagrafici, fase corrente della pipeline, ultima interazione, prossima azione programmata, lista file allegati, timeline interazioni, riepilogo finanziario (preventivi/fatture/pagamenti). *[US-02]*
- **FR-03** — L'app fornisce una ricerca testuale sui clienti per nome, telefono o tag. Il risultato è visibile entro 200 ms su un dataset di 500 clienti. *[US-03]*
- **FR-04** — L'app permette di allegare file locali (PDF, immagini) a un cliente, con limite di 10 MB per file. I file sono memorizzati nell'Internal Storage dell'app. *[US-04]*

### 6.2 Modulo Pipeline

- **FR-05** — La pipeline è composta da 10 fasi fisse: `NUOVO_LEAD`, `PRIMO_CONTATTO`, `PREVENTIVO_INVIATO`, `IN_TRATTATIVA`, `CONFERMATO`, `IN_CORSO`, `CONSEGNATO`, `IN_ATTESA_PAGAMENTO`, `CHIUSO`, `CLIENTE_RICORRENTE`. *[US-05]*
- **FR-06** — La pipeline è visualizzata in modalità Kanban (colonne scrollabili orizzontalmente) o lista raggruppata. L'utente può scegliere la modalità. *[US-05]*
- **FR-07** — Lo spostamento di un cliente fra fasi avviene tramite drag-and-drop o tramite menù dal dettaglio cliente. Ogni cambio fase è registrato nello storico con data e ora. *[US-06]*
- **FR-08** — La pipeline supporta filtri per fase, per tag e per intervallo di date. *[US-07]*

### 6.3 Modulo Task e Reminder

- **FR-09** — L'utente può creare task con titolo, descrizione, cliente collegato (opzionale), data e ora di scadenza, priorità (bassa/media/alta). *[US-08]*
- **FR-10** — Alla scadenza, l'app invia una notifica all'utente tramite NotificationChannel dedicato. La notifica è cliccabile e apre il dettaglio del task. *[US-09]*
- **FR-11** — I reminder sono persistenti: sopravvivono al riavvio del dispositivo grazie a un BroadcastReceiver che ascolta `BOOT_COMPLETED` e riarma le notifiche. *[US-09]*
- **FR-12** — L'app genera reminder automatici (suggerimenti) basati su regole: preventivo inviato senza interazioni da N giorni, cliente senza contatto da N giorni, fattura scaduta. Le regole sono configurabili nelle impostazioni. *[US-10]*

### 6.4 Modulo Note e Interazioni

- **FR-13** — L'utente può aggiungere note libere a un cliente. Le note hanno timestamp automatico e sono modificabili. *[US-11]*
- **FR-14** — L'utente può registrare un'interazione scegliendo fra tipi predefiniti (`CALL`, `MEETING`, `EMAIL`, `MESSAGGIO`, `ALTRO`), con data, durata opzionale e descrizione. *[US-12]*
- **FR-15** — Quando l'utente registra un'interazione di tipo `MEETING`, l'app può richiedere il permesso di accedere alla posizione GPS e taggare l'interazione con latitudine, longitudine e indirizzo testuale (reverse geocoding). Il tagging è opzionale e richiede consenso esplicito. *[US-13]*

### 6.5 Modulo Dashboard

- **FR-16** — La schermata "Oggi" mostra in alto: i task in scadenza oggi, i clienti senza contatto da più di N giorni, le fatture in scadenza o scadute. *[US-14]*
- **FR-17** — La schermata "Storico" presenta un riepilogo settimanale e mensile: numero di interazioni per cliente, ore tracciate per cliente, redditività (incassi / ore). Visualizzazione in forma di lista con barre proporzionali. *[US-15]*

### 6.6 Modulo Time Tracking

- **FR-18** — L'utente può avviare e fermare un timer associato a un cliente e (opzionalmente) a una descrizione di attività. Il timer continua a funzionare se l'app va in background grazie a un **Foreground Service** con notifica persistente. *[US-16]*
- **FR-19** — L'utente può aggiungere manualmente sessioni di lavoro indicando cliente, data, durata e descrizione. *[US-17]*
- **FR-20** — Per ogni cliente, l'app calcola e mostra: ore preventivate (se inserite a inizio progetto), ore reali, differenza percentuale, e ricavo orario effettivo (incasso totale / ore reali). *[US-18]*

### 6.7 Modulo Mini-finanza

- **FR-21** — L'utente può registrare un preventivo per un cliente con: importo, data invio, stato (`INVIATO`, `ACCETTATO`, `RIFIUTATO`, `SCADUTO`). *[US-19]*
- **FR-22** — L'utente può registrare una fattura con: numero, cliente, importo, data emissione, data scadenza, stato (`EMESSA`, `PAGATA`). Lo stato "In ritardo" è calcolato a runtime se data scadenza < oggi e stato ≠ `PAGATA`. *[US-20, US-21]*
- **FR-23** — La sezione finanze mostra un riepilogo del mese corrente: totale fatturato, totale incassato, totale atteso, totale in ritardo. *[US-22]*

---

## 7. Requisiti non funzionali (NFR)

### 7.1 Performance

- **NFR-01** — Apertura dell'app a schermata "Oggi" pronta entro 1.5 secondi su dispositivi target (Android 8+, 3 GB RAM).
- **NFR-02** — Ricerca clienti con risultati visibili entro 200 ms su 500 record.
- **NFR-03** — Nessuna operazione di I/O su Main Thread. Tutto il database access avviene via coroutines su `Dispatchers.IO`.

### 7.2 Persistenza e Offline-first

- **NFR-04** — L'app è pienamente funzionante senza connessione di rete. Nessuna funzionalità V1 richiede internet.
- **NFR-05** — I dati sono persistenti localmente in un database Room (SQLite). Le preferenze utente in SharedPreferences.
- **NFR-06** — Il database supporta migrazioni di schema (Room Migration) per non perdere dati fra versioni dell'app.

### 7.3 Estendibilità verso backend remoto

- **NFR-07** — L'accesso ai dati avviene **esclusivamente attraverso classi Repository**. ViewModel e UI non conoscono Room: dipendono solo dall'interfaccia del Repository.
- **NFR-08** — Ogni Repository ha una sola implementazione locale (`LocalXxxRepositoryImpl`) ma è strutturato come interfaccia, in modo che in V2 si possa aggiungere una `RemoteXxxRepositoryImpl` o una `HybridXxxRepositoryImpl` (cache locale + sync remoto) senza modificare ViewModel né schermate Compose.
- **NFR-09** — Le entità del dominio (Cliente, Task, Interazione, etc.) sono **separate** dalle entità Room. La conversione avviene nel Repository tramite mapper.

### 7.4 Privacy e permessi

- **NFR-10** — I dati personali dei clienti non lasciano mai il dispositivo in V1.
- **NFR-11** — La localizzazione (US-13, FR-15) è richiesta solo a runtime e solo nel momento in cui l'utente sceglie di taggare un meeting. Nessun tracking in background.
- **NFR-12** — Il permesso `POST_NOTIFICATIONS` (Android 13+) viene richiesto a runtime al primo avvio, contestualizzato sul valore delle notifiche.

### 7.5 Accessibilità e UX

- **NFR-13** — Tutti i Composable interattivi hanno `contentDescription` per gli screen reader.
- **NFR-14** — Il tema supporta sia Light che Dark mode (`MaterialTheme` con DayNight).
- **NFR-15** — Le aree tappabili rispettano la dimensione minima Material (48 dp).

### 7.6 Affidabilità

- **NFR-16** — I reminder schedulati sopravvivono a chiusura forzata dell'app e a riavvio del dispositivo.
- **NFR-17** — Il timer di time tracking continua a girare anche se l'app va in background (Foreground Service).
- **NFR-18** — Nessun crash sui flussi principali coperti dalle user stories.

---

## 8. Flussi utente principali

Quattro flussi che il prodotto deve gestire impeccabilmente.

### 8.1 Flusso A — Da lead a cliente confermato

**Scenario**: Giulia riceve un DM su Instagram da una potenziale cliente.

1. Giulia apre Freela e tocca il FAB "+" sulla schermata Pipeline.
2. Inserisce nome, fonte (Instagram), e una nota rapida ("chiede gestione SMM 3 mesi").
3. Il cliente viene creato in fase `NUOVO_LEAD` e appare in cima alla pipeline.
4. Dopo una call, Giulia entra nella scheda cliente, registra l'interazione "Call - 25 min - parlato di obiettivi" e sposta lo stato a `PRIMO_CONTATTO`.
5. Giulia crea un task "Inviare preventivo entro venerdì" con scadenza.
6. Inviato il preventivo, registra un nuovo record nella sezione finanze del cliente e sposta lo stato a `PREVENTIVO_INVIATO`.
7. Dopo 5 giorni senza risposta, Freela mostra automaticamente un suggerimento nella dashboard: "Hai inviato un preventivo a [Cliente] 5 giorni fa. Vuoi mandare un follow-up?"
8. Quando il cliente conferma, Giulia sposta lo stato a `CONFERMATO` e crea le tappe di consegna come task.

### 8.2 Flusso B — Sessione di lavoro tracciata

**Scenario**: Luca si siede a lavorare al sito di un cliente.

1. Luca apre Freela, va su "Oggi", e tocca "Avvia timer" sulla card del cliente.
2. Sceglie il cliente e scrive una breve descrizione ("design homepage").
3. Il timer parte; appare una notifica persistente in cima allo schermo (Foreground Service).
4. Luca chiude l'app e usa Figma per 2 ore.
5. Torna su Freela e tocca "Ferma timer" dalla notifica o dall'app.
6. La sessione viene salvata e visibile nello storico del cliente.
7. Nella scheda cliente, Luca vede aggiornato il confronto: "Preventivato: 40h | Lavorato: 18h | Ricavo orario stimato: 35€/h".

### 8.3 Flusso C — Meeting in mobilità con tag GPS

**Scenario**: Sara va a fare un sopralluogo per uno shooting.

1. Sara arriva sul posto e apre Freela.
2. Entra nella scheda del cliente e tocca "Registra interazione".
3. Sceglie il tipo `MEETING` e tocca l'icona del pin GPS.
4. L'app richiede il permesso di localizzazione (solo al primo uso). Sara accetta.
5. Freela acquisisce la posizione tramite `FusedLocationProviderClient` e fa reverse geocoding per ottenere l'indirizzo testuale.
6. L'interazione viene salvata con coordinate e indirizzo nel timeline del cliente.
7. Mesi dopo, quando Sara consulta la scheda di quel cliente, vede "Meeting del 12 marzo — Via Roma 24, Trento" e ricorda esattamente la giornata.

### 8.4 Flusso D — Sollecito pagamento gestito

**Scenario**: una fattura di Giulia è scaduta da 8 giorni.

1. Freela ha rilevato che la data scadenza è passata e lo stato non è `PAGATA`: ha generato in automatico un suggerimento.
2. Nella dashboard "Oggi", Giulia vede l'avviso: "Fattura #2025-018 a [Cliente] scaduta da 8 giorni".
3. Tocca l'avviso e arriva direttamente sul dettaglio della fattura.
4. Decide di mandare un messaggio: l'app la rimanda a WhatsApp con il numero del cliente già selezionato (intent esterno).
5. Dopo aver mandato il messaggio, registra un'interazione "Sollecito pagamento" nel timeline del cliente.
6. Quando il cliente paga, Giulia segna la fattura come `PAGATA` e il record sparisce dagli avvisi.

---

## 9. Modello dati

Il modello è progettato per Room (SQLite). Le entità di dominio sono **separate** dalle entità di persistenza (vedi NFR-09), per facilitare in futuro la sostituzione del data layer.

### 9.1 Entità

#### Cliente

| Campo | Tipo | Note |
|---|---|---|
| `id` | Long | PK, autogenerato |
| `nome` | String | NOT NULL |
| `telefono` | String? | |
| `email` | String? | |
| `fonteAcquisizione` | String? | es. "Instagram", "Passaparola" |
| `faseCorrente` | Enum | 10 valori della pipeline |
| `dataCreazione` | Long | timestamp millis |
| `note` | String? | |
| `fotoPath` | String? | path locale |
| `orePreventivate` | Float? | |
| `importoPreventivato` | Double? | |

#### Tag e ClienteTagCrossRef

Relazione molti-a-molti fra Cliente e Tag (un tag può essere su più clienti, un cliente può avere più tag).

#### Interazione

| Campo | Tipo | Note |
|---|---|---|
| `id` | Long | PK |
| `clienteId` | Long | FK Cliente |
| `tipo` | Enum | `CALL`, `MEETING`, `EMAIL`, `MESSAGGIO`, `ALTRO` |
| `data` | Long | timestamp |
| `durataMinuti` | Int? | |
| `descrizione` | String? | |
| `latitudine` | Double? | solo MEETING con consenso |
| `longitudine` | Double? | |
| `indirizzo` | String? | reverse geocoding |

#### Task

| Campo | Tipo | Note |
|---|---|---|
| `id` | Long | PK |
| `titolo` | String | |
| `descrizione` | String? | |
| `clienteId` | Long? | FK Cliente, opzionale |
| `scadenza` | Long | timestamp |
| `priorita` | Enum | `BASSA`, `MEDIA`, `ALTA` |
| `completato` | Boolean | |
| `dataCompletamento` | Long? | |
| `origine` | Enum | `MANUALE`, `SUGGERITO` |

#### SessioneLavoro (time tracking)

| Campo | Tipo | Note |
|---|---|---|
| `id` | Long | PK |
| `clienteId` | Long | FK Cliente |
| `inizio` | Long | timestamp |
| `fine` | Long? | null se sessione in corso |
| `descrizione` | String? | |
| `inserimentoManuale` | Boolean | |

#### Preventivo

| Campo | Tipo | Note |
|---|---|---|
| `id` | Long | PK |
| `clienteId` | Long | FK Cliente |
| `importo` | Double | |
| `dataInvio` | Long | |
| `stato` | Enum | `INVIATO`, `ACCETTATO`, `RIFIUTATO`, `SCADUTO` |
| `note` | String? | |

#### Fattura

| Campo | Tipo | Note |
|---|---|---|
| `id` | Long | PK |
| `numero` | String | |
| `clienteId` | Long | FK Cliente |
| `importo` | Double | |
| `dataEmissione` | Long | |
| `dataScadenza` | Long | |
| `dataPagamento` | Long? | |
| `stato` | Enum | `EMESSA`, `PAGATA` — "in ritardo" calcolato a runtime |

#### FileAllegato

| Campo | Tipo | Note |
|---|---|---|
| `id` | Long | PK |
| `clienteId` | Long | FK Cliente |
| `nomeFile` | String | |
| `path` | String | Internal Storage |
| `tipoMime` | String | |
| `dataCaricamento` | Long | |

### 9.2 Relazioni

- Un Cliente ha molte Interazioni (1:N)
- Un Cliente ha molti Task (1:N)
- Un Cliente ha molte SessioniLavoro (1:N)
- Un Cliente ha molti Preventivi e molte Fatture (1:N)
- Un Cliente ha molti FileAllegati (1:N)
- Un Cliente ha molti Tag (N:N tramite `ClienteTagCrossRef`)

---

## 10. Architettura tecnica

### 10.1 Stack scelto

Tutte le scelte sono coerenti con i materiali del corso e con la best practice ufficiale Google ("preferisci Compose per nuovi progetti", citato esplicitamente nelle slide del corso).

| Componente | Scelta |
|---|---|
| Linguaggio | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architettura | MVVM con ViewModel + StateFlow |
| Navigazione | Navigation Compose (NavHost composable) |
| Activity base | `ComponentActivity` (single-activity) |
| Persistenza dati | Room (SQLite) + SharedPreferences |
| Background work | Coroutines + WorkManager |
| Reminder schedulati | AlarmManager + BroadcastReceiver |
| Foreground Service | Per il timer di time tracking |
| Notifiche | NotificationChannel multipli + NotificationCompat |
| Localizzazione | `FusedLocationProviderClient` (Play Services) |
| Dependency Injection | Hilt (raccomandato) |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 34 |

### 10.2 Architettura a layer

Freela è organizzata in tre layer netti:

#### Layer 1 — Presentation (UI)

- Composable functions: ogni schermata è un `@Composable` in un package `ui/screens/`
- Navigation Compose con `NavHost` in `MainActivity`
- State management: gli stati di UI sono `StateFlow` esposti dai ViewModel, collected nei Composable con `collectAsStateWithLifecycle()`
- **Nessun accesso diretto a Room**: i Composable parlano solo con i ViewModel

#### Layer 2 — Domain

- ViewModel: uno per schermata complessa, condivide stato e logica
- UseCase (opzionale): incapsulano logiche di business riusabili (es. `CalcolaRedditivitaCliente`, `GeneraSuggerimentiFollowUp`)
- Model: data class del dominio puro, indipendenti da Room
- Repository interface: contratti astratti per accesso dati

#### Layer 3 — Data

- Room Database + DAO: una DAO per entità
- Repository implementation: `LocalXxxRepositoryImpl` che usa i DAO e mappa entità Room ↔ modelli di dominio
- SharedPreferences wrapper: una classe `SettingsManager` espone preferenze utente tipizzate
- File storage: allegati cliente, scritti in Internal Storage

### 10.3 Predisposizione al backend (NFR-07/08/09)

Il punto chiave dell'architettura è che il salto da "100% locale" a "backend remoto" non deve essere una riscrittura ma un'aggiunta. Per ottenere questo, in V1 manteniamo queste tre regole:

1. **Repository sempre come interfaccia**, mai come classe concreta. Esempio: `interface ClienteRepository`, implementata da `LocalClienteRepository`.
2. **Mapper espliciti** fra entità Room (annotate `@Entity`) e modelli di dominio (data class pure). In V2, gli stessi modelli di dominio verranno mappati anche da DTO di rete.
3. **Nessuna dipendenza da Room** oltre il package `data/`. I ViewModel non sanno che esiste un database: vedono solo l'interfaccia del Repository.

Quando in V2 introdurremo un'API remota, dovremo solo:

- Aggiungere un `RemoteClienteRepository` che usa Retrofit
- Oppure un `HybridClienteRepository` che fa cache locale + sync
- Cambiare l'implementazione bindata da Hilt nel modulo DI
- **Tutto il resto del codice (UI, ViewModel, Use Case) resta identico**

### 10.4 Struttura cartelle suggerita

```
app/src/main/java/com/freela/
├── MainActivity.kt
├── FreelaApp.kt                  // Application class, Hilt
├── di/                           // Moduli Hilt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
├── data/                         // Layer Data
│   ├── local/
│   │   ├── FreelaDatabase.kt     // RoomDatabase
│   │   ├── entity/               // @Entity classes
│   │   └── dao/                  // @Dao classes
│   ├── mapper/                   // Entity ↔ Domain mappers
│   ├── preferences/
│   │   └── SettingsManager.kt
│   └── repository/               // Impl concrete
│       ├── LocalClienteRepository.kt
│       └── ...
├── domain/                       // Layer Domain
│   ├── model/                    // Data class pure
│   ├── repository/               // Interfacce
│   └── usecase/
├── ui/                           // Layer Presentation
│   ├── theme/
│   ├── navigation/
│   │   └── FreelaNavHost.kt
│   ├── screens/
│   │   ├── oggi/
│   │   ├── pipeline/
│   │   ├── clienti/
│   │   ├── task/
│   │   ├── tracking/
│   │   ├── finanze/
│   │   └── settings/
│   └── components/               // Composable riusabili
├── service/
│   └── TimerForegroundService.kt
├── receiver/
│   ├── TaskReminderReceiver.kt
│   └── BootCompletedReceiver.kt
├── worker/
│   └── SuggerimentiWorker.kt
└── notification/
    └── NotificationHelper.kt
```

### 10.5 Componenti del corso esercitati

Mappa esplicita di dove ogni componente del syllabus viene utilizzato in Freela. Utile per la presentazione in sede d'esame.

| Componente del corso | Uso in Freela |
|---|---|
| Activity / Compose entry point | `MainActivity` (`ComponentActivity`) con `setContent { NavHost }` |
| Layout e Material Design | Tutte le schermate in Compose con Material 3 |
| SharedPreferences | Preferenze utente: tema, valuta, soglie reminder, primo avvio |
| SQLite / Room | Tutti i dati di dominio: clienti, task, interazioni, finanze |
| Thread / Coroutines | DAO calls su `Dispatchers.IO` via `viewModelScope` |
| Service (Foreground) | Timer di time tracking con notifica persistente |
| WorkManager | Generazione periodica dei suggerimenti di follow-up |
| AlarmManager | Notifiche puntuali alla scadenza dei task |
| BroadcastReceiver | `BOOT_COMPLETED` per riarmare le notifiche dopo riavvio |
| Notifiche | Canali multipli (reminder task, sollecito pagamento, timer) |
| Sensori / Localizzazione | Tag GPS dei meeting (`FusedLocationProvider` + reverse geocoding) |
| Permessi runtime | `POST_NOTIFICATIONS`, `ACCESS_FINE_LOCATION` (contestuale) |

---

## 11. Roadmap

### 11.1 V1 — MVP (oggetto del progetto d'esame)

Tutte e 7 le funzioni descritte nello Scope (sezione 4.1), 100% offline, persistenza locale, architettura predisposta per backend.

### 11.2 V2 — Cloud sync e smart features

- Backend remoto con autenticazione utente
- Sincronizzazione multi-device
- Preventivi con template smart (modelli pre-compilati per tipologia di lavoro)
- Sincronizzazione con Google Calendar / iCalendar
- Template di messaggi predefiniti (follow-up, richiesta materiali, sollecito pagamento)
- Notifiche contestuali (es. notifica all'ingresso in una location associata a un cliente)

### 11.3 V3 — AI assistant

- Riassunto automatico dello storico cliente
- Generazione di una bozza di preventivo a partire da un brief testuale
- Predizione di clienti a rischio inattività
- Suggerimento dei follow-up più efficaci basato sullo storico personale

### 11.4 Sequenza implementativa V1 consigliata

Ordine di sviluppo che minimizza rilavorazioni:

1. Setup progetto: Gradle, Compose, Hilt, Room, Navigation
2. Modello dati: entità Room, DAO, Repository interface, mapper
3. Modulo Clienti (CRUD scheda cliente) — primo flusso end-to-end completo
4. Pipeline visuale (riusa lo stesso modello Cliente)
5. Note e Interazioni (interazioni senza GPS in prima battuta)
6. Task base + AlarmManager + Notifiche puntuali
7. BroadcastReceiver per `BOOT_COMPLETED` (test su riavvio)
8. Time tracking con Foreground Service
9. Mini-finanza (preventivi, fatture, stato)
10. Dashboard "Oggi" e "Storico"
11. WorkManager per suggerimenti automatici
12. Integrazione GPS sul tag interazione di tipo Meeting
13. Polish: Dark mode, accessibilità, gestione errori, animazioni

---

## 12. Rischi e mitigazioni

| Rischio | Severità | Mitigazione |
|---|---|---|
| Scope troppo ampio per 2 persone | Alto | Implementare nell'ordine della sezione 11.4. Time tracking e mini-finanza sono i primi candidati al taglio se mancasse il tempo. |
| Foreground Service ucciso dal sistema | Medio | Usare le best practice viste nel materiale del corso (notifica permanente, `START_NOT_STICKY` corretto, dichiarazione nel manifest). |
| `BOOT_COMPLETED` non riceve i reminder | Medio | Su alcuni vendor (Xiaomi, Huawei) le autostart sono limitate. Documentare il limite e testare su almeno 2 device fisici diversi. |
| Permessi GPS rifiutati dall'utente | Basso | Il tag GPS è opzionale. L'interazione si salva comunque senza coordinate. |
| Migrazione Room rotta tra versioni dev | Basso | Usare `destructiveMigration` solo in build di sviluppo. In release definire `Migration` esplicite. |
| Difficoltà a coordinare il lavoro a 2 | Medio | Suddividere per moduli (Mattia: Clienti + Pipeline + Dashboard; Riccardo: Task + Time tracking + Finanze). Repository condivisi. |

---

## 13. Appendice

### 13.1 Competitor matrix

| Prodotto | Posizionamento |
|---|---|
| HoneyBook | Piattaforma all-in-one matura, ma orientata small business, pesante per il singolo professionista |
| HubSpot | CRM potente ma pensato per team commerciali e processi B2B strutturati, sovradimensionato per il freelancer |
| Bigin (Zoho) | Il più vicino concettualmente, ma resta un CRM small business generalista, non costruito sulla giornata del freelancer |
| Bonsai | Forte su time tracking e fatture, debole su relazione cliente e pipeline operativa |
| **Freela** | **CRM mobile-first per il singolo freelancer, focalizzato sulla giornata reale: chi contattare, cosa consegnare, chi deve pagare** |

### 13.2 Glossario

- **Pipeline**: sequenza di fasi attraverso cui passa un cliente, dal primo contatto alla fine del rapporto.
- **Lead**: contatto potenziale, non ancora cliente confermato.
- **Follow-up**: azione di richiamo verso un contatto in stato di attesa (es. preventivo non risposto).
- **Foreground Service**: servizio Android che richiede una notifica permanente e ha priorità alta contro la chiusura dal sistema.
- **Repository pattern**: layer di astrazione fra logica di dominio e fonte dati (database, rete, cache).
- **MVVM**: Model-View-ViewModel, pattern architetturale che separa lo stato della UI (ViewModel) dalla sua rappresentazione (View).
- **State hoisting**: pratica di Compose che porta lo stato verso l'alto nella gerarchia dei componenti per renderli riutilizzabili e testabili.

### 13.3 Documento — meta

- Versione PRD: 1.0
- Stato: Draft pre-implementazione
- Prossima revisione: alla fine della prima iterazione (modulo Clienti completato)

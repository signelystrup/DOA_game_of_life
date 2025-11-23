\## Evaluering af valg af algoritmer og datastrukturer



\### Flocking

Simulationen anvender en blanding af flocking og anti-flocking. Det har vi valgt, fordi kaniner bør flygte fra ulve, og dyr af samme race bør finde sammen for at formere sig, efter de har spist. 



Vi har dog ikke noget alignment, så dyr af samme race følges ikke ad. Til gengæld har hvert dyr en liste med prioriteter, som de bruger for at finde ud af, hvor de skal gå hen.



Vi har også talt om pathfinding, men eftersom dyrene ikke har speciel lang synsvidde, besluttede vi, at det ikke giver mening ift. simulationens interne logik. En kanin kan ikke se langt nok til at vide, hvordan den kommer rundt om et hegn. 



\### Spatial hashing (spatial partitioning)

Vi har brugt spatial hashing for at forbedre performance ifb. med søgning af omkringliggende entities ved ikke at hente alle entities i systemet, men kun dem, der er stort set inden for synsvidde. 



Vi satte et grid op i et 2D-array, da det er mest effektivt i forhold til insert- og lookup-kommandoer og kører med en O(1) konstant runtime. Det sikrer, at insert- og lookup-kommandoer kører med samme hastighed, selvom der er mange entities. Og siden griddet ikke ændrer størrelse, kan man bruge et 2D-array, hvor hver celle indeholder en arrayliste med alle de ulve, kaniner, græs og hegn, som ligger indenfor cellens areal.



Alternativt kunne man også have valgt at anvende et hashmap, med cellens x og y koordinator som key, og en liste med entities som value. Her vil lookup også være O(1), og man vil spare hukommelse på tomme celler. Desuden kan hashmappet gøre griddets størrelse dynamisk, men det er ikke nødvendigt at gøre brug af den funktion, og hashmaps bruger længere tid per remove- og insert-operation. Derfor har vi vurderet, at et 2D-array vil være smartere, da vi har mange entities, som bevæger sig mellem celler.




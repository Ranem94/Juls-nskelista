# Juls önskelista

En enkel önskelista-app byggd med Jetpack Compose.

Funktioner
- Lägga till presenter med namn och kategori (Jul, Födelsedag, Övrigt)
- Filtrera presenter efter kategori
- Markera presenter som klara med Checkbox
- Redigera namn på presenter
- Ta bort presenter med Ångra-funktion (Snackbar)
- Statistik: visar hur många presenter som är klara, t.ex. "2 av 5 klara"
- Sparas automatiskt med SharedPreferences


 1. Felsökning

Problemet:
När jag testade appen så hamnade alla nya presenter alltid i kategorin Jul, även när jag hade valt Födelsedag eller Övrigt. Det var väldigt förvirrande för appen visade en kategori men sparade i en annan.

Varför det blev fel:
Jag hittade två fel som hängde ihop:

1. I `PresentViewModel` i funktionen `addPresent()` hade jag skrivit `"Jul"` fast i koden:
`Present(nextId++, nyText.trim(), "Jul", false)`
Det betydde att alla presenter fick kategorin Jul, oavsett vad jag hade valt.

2. I `MainActivity` hade jag två nästan likadana rader med FilterChip: en rad som hette "Du lägger till i" och en som hette "Visa". Jag tryckte på "Visa"-raden och trodde att jag ändrade var presenten skulle sparas, men den raden ändrade bara vad som visades på skärmen.

Så här löste jag det:
1. Jag bytte ut `"Jul"` mot variabeln `valdKategori`:
`Present(nextId++, nyText.trim(), valdKategori, false)`
Nu sparas presenten i den kategori som faktiskt är vald.

2. Jag tog bort den ena raden och har nu bara en enda rad med FilterChip. När man trycker på en kategori så uppdateras båda variablerna samtidigt:
`valdFilter = k.namn`
`valdKategori = k.namn`
På så sätt är det man ser på skärmen alltid samma som dit nya presenter sparas. Efter det testade jag att lägga till presenter i alla tre kategorier och det fungerade.

 2. Hjälpmedel
 AI var som en lärare för mig under det här provet. Jag frågade vad felet var, varför det blev fel och hur jag kunde fixa det. Den förklarade steg för steg hur jag skulle rätta till det, och då förstod jag bättre varför koden inte fungerade.

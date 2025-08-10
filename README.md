# Book Depo

Demó alkalmazás, amely bemutatja a modern android fejlesztés néhány fő koncepcióját.

## Koncepciók

* Rétegezettség
* Offline first
* Single source of truth
* Coroutine, Flow
* Composable
* Izolált komponensek a tesztelhetőség miatt
* Interfészekhez való alkalmazkodás
* Lifecycle

## Részletesebben a fentiek

### Rétegezettség

Legalul helyezkedik el a lokális adatbázis (SQLite és Room). Ebbe perzisztáljuk a `@Entity` osztályokat, amelyek között - ha szükséges - relációkat állítunk fel.

Az adatbázison végzett alapvető műveletek (CRUD) a __DAO rétegen__ keresztül valósulnak meg, ezek mindig `@Dao`-val annotált osztályok.

A dao felett eggyel a repository réteg helyezkedik el, ha szükség van ilyenre. Ennek a feladata jellemzően, hogy több különböző forrás között végezzen 
adat illesztést. Például ha egy távoli szerverről kérünk le adatokat, azok nem biztos, hogy olyan struktúrában érkeznek, ahogy a mobil app-nak arra szüksége
van. A repository réteg konvertálja a szervertől kapott adatokat az alkalmazás adatmodelljére, majd az alatta elhelyezkedő dao használatával perzisztálja azokat
a lokális adatbázisba.

A repository (vagy ha nincs az alkalmazásban repository réteg, akkor a dao) az elsődleges adatforrás a view modell számára. A view modell lifecycle képes
komponens az androidban, azaz bármilyen taszkot indít, azok szabályosan meg lesznek szüntetve, amikor a view modell életciklusát az operációs rendszer
ill. az Android framework lezárja.

Activity, Fragment vagy Composable: ezek alkothatják egy android app felhasználói felületét (UI). Nagyon fontos, hogy ma már reaktív, dekleratív UI-kat írunk.
A UI aktuális kinézete mindig az ún. __state függvénye__. 
A state-ről a view modell gondoskodik, így a UI számára a source of the truth maga a view modell.

### Offline first

Egyre nagyobb az igény arra, hogy a mobil app-ok teljeskörűen használhatóak legyenek a hálózati kapcsolat ideiglenes hiánya esetén is. Ehhez kell illeszteni
az alkalmazás architektúráját.

### Single source of truth

Megállapodás kérdése, hogy ez melyik réteg. Ha a használat feltételei olyanok, hogy mindig elérhető megbízható hálózati kapcsolat, akkor lehet akár a szervertől
kapott adat is. Ha nincs mindig elérhető hálózat működés közben, akkor a lokális adatbázis a legjobb választás. Memóriában tartott adatstruktúrákra alapozni
nem ideális, mert használat közben gyakran kerül az alkalmazás háttérbe, majd újra előtérbe; ennek során az android alkalmazás komponensek bármikor 
újra példányosodhatnak a rendszer által, ami a lokálisan nem tárolt adatok elvesztéséhez vezet.

### Coroutine, Flow

A Kotlin nyelv natív megoldásai a többszálúság és a reaktív programozás témakörében. Nagyon fontos axióma, hogy minden adatlekérés, erőforrás igényes számolás,
szerver hívás, lokális adatbázissal való kommunikáció a main thread-től izoláltan kell hogy végrehajtódjon. A main thread csak a UI számára van fenntartva.

### Composable

A modern android UI alapvető építőköve. Egy teljesen új paradigmát is hoz magával: a régi imperatív UI fejlesztés helyett a deklaratív szemléletet.

### Izolált komponensek

Komponens alatt itt egyaránt értendő a UI és egyéb alkalmazás komponesnek is. Mindent olyan szemléletben kell lefejleszteni, hogy könnyen kiemelhető legyen
a saját "futási környezetéből" és önállóan tesztelhető legyen. Egy view modell nem tesztelhető önállóan, ha a kódja `@Composable` függvényeket is tartalmaz.
Egy komponens akkor sem tesztlehető izoláltan, ha saját maga hozza létre a dependenciáit. Így erősen javaolt egy DI framework használata. Egy `@Composable` 
nem tesztelehő izoláltan, __sőt, még egy preview sem készíthető el a számára__, ha view modell függősége van.


### Interfészek

A UI réteg felett minden rétegben ajánlott a komponensek számára interész létrehozása. Ennek egyik előnye, hogy végig kell idejekorán gondolni, mire lesz majd képes
az adott osztály. Ha több fejlesztő dolgozik az alkalmazás összeérő részein, egy jól megfogalmazott interész mentén mindki tud haladni a saját munkájával, 
__nem kell egymásra várni__. További előnye az interfészre való illeszkedésnek, hogy teszteléskor az ilyen komponensek sokkal könnyebben mockolhatók, mint azok,
amelyek viselkedését nem írja le egy interfész. Az interfészre elég ránézni, és körülbelül tudjuk, mire való az adott komponens. Visszatartja a fejlesztőt
attól, hogy ún. _god object_-et írjon, azaz megpróbáljon mindent belezsúfolni egy adott osztályba. Elősegíti a __sepration of concerns__ alapelv betartását,
ami szintén annak a megfogalmazása, hogy minden osztálynak legyen egy __szigorú, jól behatárolt felelősségi köre__ és annél többet ne akarjon a fejlesztő 
megvalósítani benne.

### Lifecycle

Android esetében ez a kezdetektől jelen van és megkerülhetetlen. A komponensek folyamatosan létrejönnek és elpusztulnak, miközben az android rendszer
próbálja menedzselni a rendelkezésre álló erőforrásokat. De a felhasználó saját maga is bármikor visszanavigálhat, félbehagyhatja azt, amit éppen csinál.
Ezért fejlesztés közben mindig szem előtt kell tartanunk, hogy az alkalmazásban indított háttérszálakon futó taszkok be tudnak-e fejeződni, 
és amennyiben félbeszakadnak, az nem okoz-e memory leak-et, illetve performancia vagy user experience szempontjából súlyos hibát.



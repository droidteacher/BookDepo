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

Legalul helyezkedik el a __lokális adatbázis__ (SQLite és Room). Ebbe perzisztáljuk a `@Entity` osztályokat, amelyek között - ha szükséges - relációkat állítunk fel.

Az adatbázison végzett alapvető műveletek (CRUD) a __DAO rétegen__ keresztül valósulnak meg, ezek mindig `@Dao`-val annotált osztályok.

A dao felett eggyel a __repository réteg__ helyezkedik el, ha szükség van ilyenre. Ennek a feladata jellemzően, hogy több különböző forrás között végezzen 
adat illesztést. Például ha egy távoli szerverről kérünk le adatokat, azok nem biztos, hogy olyan struktúrában érkeznek, ahogy a mobil app-nak arra szüksége
van. A repository réteg konvertálja a szervertől kapott adatokat az alkalmazás adatmodelljére, majd az alatta elhelyezkedő dao használatával perzisztálja azokat
a lokális adatbázisba.

A repository (vagy ha nincs az alkalmazásban repository réteg, akkor a dao) az elsődleges adatforrás a __view modell__ számára. A view modell életciklussal rendelkező
komponens az androidban. Ez azért fontos, mert ha bármilyen háttérfolyamatot indítunk egy életciklussal rendelkező komponensből, ügyelni kell arra is,
hogy ezek a folyamatok megfelelően véget érjenek, amikor a komponens életciklusa véget ér. Erre beépített mechanizmusok léteznek, amelyeket nem célszerű megkerülni, 
vagy figyelmen kívül hagyni.


Activity, Fragment vagy Composable: ezek alkothatják egy android app felhasználói felületét (UI). Nagyon fontos, hogy ma már reaktív, dekleratív UI-kat írunk.
A UI aktuális kinézete mindig az ún. __state függvénye__. 
A state-ről a view modell gondoskodik, így a UI számára a source of the truth maga a view modell.

### Offline first

Egyre nagyobb az igény arra, hogy a mobil app-ok teljeskörűen használhatóak legyenek a hálózati kapcsolat ideiglenes hiánya esetén is. Ehhez kell illeszteni
az alkalmazás architektúráját.

### Single source of truth

Megállapodás kérdése, hogy ez melyik réteg. Ha a használat feltételei olyanok, hogy mindig elérhető megbízható hálózati kapcsolat, akkor lehet akár a szervertől
kapott adat is, amelyet memóriában tartunk. Ilyenkor egy adott képernyőre navigáláskor minden esetben elindul egy hálózati kérés, amely a szervertől lehúzza a legfirsebb állapotot.
Ha nincs mindig elérhető hálózat működés közben, akkor a lokális adatbázis a legjobb választás. Memóriában tartott adatstruktúrákra alapozni
nem ideális, mert használat közben gyakran kerül az alkalmazás háttérbe, majd újra előtérbe; ennek során az android alkalmazás komponensek bármikor 
újra példányosodhatnak a rendszer által, ami a lokálisan nem tárolt adatok elvesztéséhez vezet.

### Coroutine, Flow

A Kotlin nyelv natív megoldásai a többszálúság és a reaktív programozás témakörében. Nagyon fontos axióma, hogy minden adatlekérés, erőforrás igényes számolás,
szerver hívás, lokális adatbázissal való kommunikáció a main thread-től izoláltan kell hogy végrehajtódjon. A main thread csak a UI számára van fenntartva.

### Composable

A modern android UI alapvető építőköve. Egy teljesen új paradigmát is hoz magával: a régi imperatív UI fejlesztés helyett a deklaratív szemléletet. 

A Compose komponensek lehetőleg legyenek stateless-ek, állapot nélküliek. Az ún. state hoisting segítségével az állapotot emeljük ki a composable-ből 
és mozgassuk át magassabban lévő wrapper komponensbe, de még jobb, ha egészen a view modellig. 

Akinél az állapotot tároló/megváltoztató kódrészlet van, az a réteg a source of truth. Ha a UI komponens szemszögéből vizsgáljuk, akkor az állapot mindig fentről lefelé adódik át 
a rétegeken keresztül. A felhasználó által kiváltott események pedig lentről felfelé mozognak a view modellig.

__Semmiképpen ne írjunk olyan kesze-kusza logikát, ahol a UI state több résztvevő által is frissülhet!__

### Izolált komponensek

Komponens alatt itt egyaránt értendő a UI és egyéb alkalmazás komponesnek is. 

Mindent olyan szemléletben kell lefejleszteni, hogy a saját "futási környezetéből" könnyen kiemelhető és önállóan tesztelhető legyen. 

Egy view modell nem tesztelhető önállóan, ha a kódja `@Composable` függvényeket is tartalmaz.

Egy komponens akkor sem tesztlehető izoláltan, ha saját maga hozza létre a dependenciáit. Így erősen javasolt egy DI framework használata. 

Az a `@Composable`, amelynek view modell függősége van, nagyon nehezen tesztelhető izoláltan. Ezért egy generikusnak szánt komponens esetében az a helyes,
ha csak teljesen "neutrális" paramétereket vesz át, gyakorlatilag a Kotlin alap adattípusait (String, Int, Boolean stb.). Általában a jól megírt generikus komponens
újrafelhasználható, nagyon könnyű hozzá preview-t készíteni és teszt esetet írni a működéséhez.


### Interfészek

A UI-nál magasabban lévő rétegek esetében ajánlott a komponensek számára interész létrehozása. Ennek egyik előnye, hogy idejekorán végig kell gondolni, mire lesz majd képes
az adott osztály. Ha több fejlesztő dolgozik az alkalmazás összeérő részein, egy jól megfogalmazott interész mentén mindenki tud haladni a saját munkájával, 
__nem kell egymásra várni__. 

További előnye az interfészre való illeszkedésnek, hogy teszteléskor az ilyen komponensek sokkal könnyebben mock-olhatóak, mint azok,
amelyek viselkedését nem írja le egy interfész. 

Az interfészre elég ránézni, és körülbelül tudjuk, mire való az adott komponens. Visszatartja a fejlesztőt
attól, hogy ún. _god object_-et írjon, azaz megpróbáljon mindent belezsúfolni egy adott osztályba. 

Elősegíti a __sepration of concerns__ alapelv betartását, ami szintén annak a megfogalmazása, hogy minden osztálynak legyen egy 
__szigorú, jól behatárolt felelősségi köre__ és ennél többet ne is akarjon a fejlesztő megvalósítani benne.

### Lifecycle

Android esetében ez a kezdetektől jelen van és megkerülhetetlen. A komponensek folyamatosan létrejönnek és elpusztulnak, miközben az android rendszer
próbálja menedzselni a rendelkezésre álló erőforrásokat. Amint a felhasználó ide-oda navigál az alkalmazáson belül,
folyamatosan épül és lebomlik a back stack, ami szintén életciklus eseményeket generál. 

Ezért fejlesztés közben mindig szem előtt kell tartanunk, hogy az alkalmazásban indított háttérszálakon futó taszkok be tudnak-e fejeződni, 
és amennyiben félbeszakadnak, az nem okoz-e memory leak-et, vagy performancia/user experience szempontjából súlyos hibát.



package com.eurecalab.eureca;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.eurecalab.eureca.constants.GenericConstants;
import com.eurecalab.eureca.constants.DynamoDBAction;
import com.eurecalab.eureca.constants.S3Action;
import com.eurecalab.eureca.core.Callable;
import com.eurecalab.eureca.core.Category;
import com.eurecalab.eureca.core.GlobalState;
import com.eurecalab.eureca.core.Recording;
import com.eurecalab.eureca.net.CategoriesAsyncTaskAdmin;
import com.eurecalab.eureca.net.DynamoDBTask;
import com.eurecalab.eureca.net.S3Task;
import com.eurecalab.eureca.net.SignInTask;
import com.eurecalab.eureca.ui.ThemeSwitcher;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class AdminActivity extends AppCompatActivity implements Callable, OnClickListener, GoogleApiClient.OnConnectionFailedListener {
    private GlobalState gs;
    private Button categorie;
    private Button suoni;
    private Button bonifica;
    private Button test;
    private SignInButton signInButton;
    private Button signOutButton;
    private TextView mStatusTextView;
    private Toolbar toolbar;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    public static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeSwitcher(this).onActivityCreateSetTheme();
        setContentView(R.layout.activity_standalone);

        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        gs = (GlobalState) getApplication();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_admin, container, false);

            categorie = (Button) rootView.findViewById(R.id.categorie);
            suoni = (Button) rootView.findViewById(R.id.suoni);
            bonifica = (Button) rootView.findViewById(R.id.bonifica);
            test = (Button) rootView.findViewById(R.id.test);
            categorie.setOnClickListener(AdminActivity.this);
            suoni.setOnClickListener(AdminActivity.this);
            bonifica.setOnClickListener(AdminActivity.this);
            test.setOnClickListener(AdminActivity.this);

            signInButton = (SignInButton) rootView.findViewById(R.id.sign_in_button);
            signInButton.setSize(SignInButton.SIZE_STANDARD);
            signInButton.setOnClickListener(AdminActivity.this);

            mStatusTextView = (TextView) rootView.findViewById(R.id.status_text_view);
            signOutButton = (Button) rootView.findViewById(R.id.sign_out_button);
            signOutButton.setOnClickListener(AdminActivity.this);

            return rootView;
        }
    }

    private void restoreCategories() {
        Category cat = new Category();
        cat.setName("Youtube");
        cat.setColorHex("#EE111A");
        cat.setIconFileName("youtube_logo.png");
        cat.setSortIndex(GenericConstants.YOUTUBE_SORT_INDEX);
        DynamoDBTask persister = new DynamoDBTask(this, null, cat, null, null, DynamoDBAction.CATEGORY);
        persister.execute();

        cat = new Category();
        cat.setName("Disney");
        cat.setColorHex("#006BB3");
        cat.setIconFileName("disney_logo.png");
        cat.setSortIndex(GenericConstants.DISNEY_SORT_INDEX);
        persister = new DynamoDBTask(this, null, cat, null, null, DynamoDBAction.CATEGORY);
        persister.execute();

        cat = new Category();
        cat.setName("Cartoni");
        cat.setColorHex("#FF7800");
        cat.setIconFileName("cartoon_logo.png");
        cat.setSortIndex(GenericConstants.CARTOONS_SORT_INDEX);
        persister = new DynamoDBTask(this, null, cat, null, null, DynamoDBAction.CATEGORY);
        persister.execute();

        cat = new Category();
        cat.setName("Film");
        cat.setColorHex("#FFC107");
        cat.setIconFileName("movie_logo.png");
        cat.setSortIndex(GenericConstants.MOVIE_SORT_INDEX);
        persister = new DynamoDBTask(this, null, cat, null, null, DynamoDBAction.CATEGORY);
        persister.execute();

        cat = new Category();
        cat.setName("Politici");
        cat.setColorHex("#333333");
        cat.setIconFileName("politics_logo.png");
        cat.setSortIndex(GenericConstants.POLITICS_SORT_INDEX);
        persister = new DynamoDBTask(this, null, cat, null, null, DynamoDBAction.CATEGORY);
        persister.execute();

        cat = new Category();
        cat.setName("Calcio");
        cat.setColorHex("#007A08");
        cat.setIconFileName("soccer_logo.png");
        cat.setSortIndex(GenericConstants.SOCCER_SORT_INDEX);
        persister = new DynamoDBTask(this, null, cat, null, null, DynamoDBAction.CATEGORY);
        persister.execute();

        cat = new Category();
        cat.setName("Dialetti");
        cat.setColorHex("#521E42");
        cat.setIconFileName("calabria_logo.png");
        cat.setSortIndex(GenericConstants.DIALETTI_SORT_INDEX);
        persister = new DynamoDBTask(this, null, cat, null, null, DynamoDBAction.CATEGORY);
        persister.execute();

        cat = new Category();
        cat.setName("Serie Tv");
        cat.setColorHex("#6994a8");
        cat.setIconFileName("tv_series_logo.png");
        cat.setSortIndex(GenericConstants.TV_SERIES_SORT_INDEX);
        persister = new DynamoDBTask(this, null, cat, null, null, DynamoDBAction.CATEGORY);
        persister.execute();

        cat = new Category();
        cat.setName("Effetti Sonori");
        cat.setColorHex("#B56D2F");
        cat.setIconFileName("sound_logo.png");
        cat.setSortIndex(GenericConstants.SOUND_EFFECTS_SORT_INDEX);
        persister = new DynamoDBTask(this, null, cat, null, null, DynamoDBAction.CATEGORY);
        persister.execute();

        cat = new Category();
        cat.setName("Preferiti");
        cat.setColorHex("#FFFF00");
        cat.setIconFileName("favorites_logo.png");
        cat.setSortIndex(GenericConstants.FAVORITES_SORT_INDEX);
        persister = new DynamoDBTask(this, null, cat, null, null, DynamoDBAction.CATEGORY);
        persister.execute();
    }

    private void changeCategoriesColorAndIcon() {
        for (Category cat : gs.getCategories()) {
            if (cat.getName().equals("Youtube")) {
                cat.setColorHex("#EE111A");
                cat.setIconFileName("youtube_logo.png");
                cat.setSortIndex(GenericConstants.YOUTUBE_SORT_INDEX);
            } else if (cat.getName().equals("Disney")) {
                cat.setColorHex("#006BB3");
                cat.setIconFileName("disney_logo.png");
                cat.setSortIndex(GenericConstants.DISNEY_SORT_INDEX);
            } else if (cat.getName().equals("Cartoni")) {
                cat.setColorHex("#FF7800");
                cat.setIconFileName("cartoon_logo.png");
                cat.setSortIndex(GenericConstants.CARTOONS_SORT_INDEX);
            } else if (cat.getName().equals("Film")) {
                cat.setColorHex("#FFC107");
                cat.setIconFileName("movie_logo.png");
                cat.setSortIndex(GenericConstants.MOVIE_SORT_INDEX);
            } else if (cat.getName().equals("Politici")) {
                cat.setColorHex("#333333");
                cat.setIconFileName("politics_logo.png");
                cat.setSortIndex(GenericConstants.POLITICS_SORT_INDEX);
            } else if (cat.getName().equals("Calcio")) {
                cat.setColorHex("#007A08");
                cat.setIconFileName("soccer_logo.png");
                cat.setSortIndex(GenericConstants.SOCCER_SORT_INDEX);
            } else if (cat.getName().equals("Dialetti")) {
                cat.setColorHex("#521E42");
                cat.setIconFileName("calabria_logo.png");
                cat.setSortIndex(GenericConstants.DIALETTI_SORT_INDEX);
            } else if (cat.getName().equals("Serie Tv")) {
                cat.setColorHex("#6994a8");
                cat.setIconFileName("tv_series_logo.png");
                cat.setSortIndex(GenericConstants.TV_SERIES_SORT_INDEX);
            } else if (cat.getName().equals("Effetti Sonori")) {
                cat.setColorHex("#B56D2F");
                cat.setIconFileName("sound_logo.png");
                cat.setSortIndex(GenericConstants.SOUND_EFFECTS_SORT_INDEX);
            } else if (cat.getName().equals("Preferiti")) {
                cat.setColorHex("#FFFF00");
                cat.setIconFileName("favorites_logo.png");
                cat.setSortIndex(GenericConstants.FAVORITES_SORT_INDEX);
            }

            DynamoDBTask persister = new DynamoDBTask(this, null, cat, null, null, DynamoDBAction.CATEGORY);
            persister.execute();
        }

    }

    private void saveAllSounds() {
        List<String> list = new LinkedList<>();
        list.add("Aggrizzan i carni+Aggrizzan i carni Masterchef Cosenza Cosentino Chef Ivan Ivanuccio Dialetti+Dialetti");
        list.add("Chiavat a na turra+Masterchef Cosenza Cosentino Chef Ivan Ivanuccio Chiavat a na turra Dialetti+Dialetti");
        list.add("E' miegl+E' miegl Masterchef Cosenza Cosentino Chef Ivan Ivanuccio Dialetti+Dialetti");
        list.add("Pia ass ppe figura+Masterchef Cosenza Cosentino Chef Ivan Ivanuccio Carte Pia ass ppe figura Dialetti+Dialetti");
        list.add("Subito ti mint a chiangiari+Masterchef Cosenza Cosentino Chef Ivan Ivanuccio Subito ti mint a chiangiari Dialetti+Dialetti");
        list.add("Tu si nnu vigliacch fra+Masterchef Cosenza Cosentino Chef Ivan Ivanuccio Tu si nnu vigliacch fra Dialetti+Dialetti");
        list.add("E io scommetto Stewie+The Griffin E io scommetto Stewie Cartoni Toro meccanico Verginità+Cartoni");
        list.add("Gran bei momenti Stewie+The Griffin Gran bei momenti Stewie cartoni Donne Cartoni+Cartoni");
        list.add("Mamma Mammina Stewie+The Griffin Mamma Mammina Stewie Lois Cartoni+Cartoni");
        list.add("Mi fai semplicemente schifo Stewie+The Griffin Mi fai semplicemente schifo Stewie Cartoni+Cartoni");
        list.add("Se la tortura non funziona Stewie+The Griffin Se la tortura non funziona Stewie tenerezza Cartoni+Cartoni");
        list.add("Torna subito qui Stewie+The Griffin Torna subito qui Stewie amami Cartoni+Cartoni");
        list.add("De ih ih oh Homer+The Simpsons De ih ih oh Homer Cartoni+Cartoni");
        list.add("Doh Homer+The Simpsons Doh Homer Cartoni+Cartoni");
        list.add("Miiitico Homer+The Simpsons Miiitico Mitico Homer Cartoni+Cartoni");
        list.add("Avvoltoi annoiati nella giungla 2 Mowgli+Avvoltoi annoiati nella giungla 2 Mowgli Cosa facciamo qualcosa Disney+Disney");
        list.add("Luhaaa Il Re Leone+Luhaaa Il Re Leone Disney+Disney");
        list.add("Maledetta barba La spada nella roccia+Maledetta barba La spada nella roccia avrebbe funzionato Disney+Disney");
        list.add("Mi sono sembrate così eleganti Hercules+Mi sono sembrate così eleganti Hercules Disney+Disney");
        list.add("Sabotaaaaggio Cenerentola+Sabotaaaaggio Cenerentola sabotaggio+Disney");
        list.add("Ti ha appena fatto una domanda Mulan+Ti ha appena fatto una domanda Mulan ufficiale comandante Disney+Disney");
        list.add("Toy Story 1 - Cadere con stile+Toy Story 1 - Cadere con stile Disney Woody+Disney");
        list.add("Toy Story 1 - Per la peppa e la peppina+Toy Story 1 - Per la peppa e la peppina Woody+Disney");
        list.add("Tu chi sei Biancaneve+Tu chi sei mia cara Biancaneve Disney+Disney");
        list.add("Effetti Sonori  - Applausi+Effetti Sonori  - Applausi+Effetti Sonori");
        list.add("Effetti Sonori - Allarme rosso+Effetti Sonori - Allarme rosso+Effetti Sonori");
        list.add("Effetti Sonori - Frenata+Effetti Sonori - Frenata+Effetti Sonori");
        list.add("Effetti Sonori - Frustata+Effetti Sonori - Frustata+Effetti Sonori");
        list.add("Effetti Sonori - Il mare+Effetti Sonori - Il mare+Effetti Sonori");
        list.add("Effetti Sonori - Pioggia+Effetti Sonori - Pioggia+Effetti Sonori");
        list.add("Effetti Sonori - Pollaio+Effetti Sonori - Pollaio+Effetti Sonori");
        list.add("Effetti Sonori - Porcile+Effetti Sonori - Porcile+Effetti Sonori");
        list.add("Effetti Sonori - Rullo di tamburi+Effetti Sonori - Rullo di tamburi+Effetti Sonori");
        list.add("Effetti Sonori - Sega+Effetti Sonori - Sega+Effetti Sonori");
        list.add("Effetti Sonori - Traffico+Effetti Sonori - Traffico+Effetti Sonori");
        list.add("Effetti Sonori - Vetri infranti+Effetti Sonori - Vetri infranti+Effetti Sonori");
        list.add("Dracula Bevi+Dracula Untold Dracula Bevi Film+Film");
        list.add("Dracula Che cosa stai Cercando+Dracula Untold Dracula Che cosa stai Cercando Film+Film");
        list.add("Frankenstein Junior Lupu Ululà+Frankenstein Junior Frankenstein Junior Lupu Ululà castello ululi Film+Film");
        list.add("Frankenstein Junior Si può Fare+Frankenstein Junior Frankenstein Junior Si può Fare Film+Film");
        list.add("Alle scale piace cambiare+Harry Potter Alle scale piace cambiare scale Film+Film");
        list.add("Cicatrice+Harry Potter Cicatrice Ron Film+Film");
        list.add("Harry Potter Suu+Harry Potter Harry Potter Suu+Film");
        list.add("La camera dei segreti è Stata Aperta+Harry Potter La camera dei segreti è Stata Aperta silente Film+Film");
        list.add("La camera sia la dimora di un mostro+Harry Potter La camera sia la dimora di un mostro mcgranitt Film+Film");
        list.add("O peggio espellere+Harry Potter O peggio espellere hermione granger uccidere Film+Film");
        list.add("Tu sei un mago Harry+Harry Potter Tu sei un mago Harry Hagrid Film+Film");
        list.add("Fuggite sciocchi+Il Signore degli Anelli Fuggite sciocchi Gandalf Film+Film");
        list.add("Il mio tesssoro+Il Signore degli Anelli Il mio tesssoro anello tesoro Film+Film");
        list.add("Non é questo il giorno+Il Signore degli Anelli Non é questo il giorno FIlm+Film");
        list.add("They're Taking the Hobbits to Isengard+Il Signore degli Anelli They're Taking the Hobbits to Isengard Legolas Film+Film");
        list.add("Tu non puoi passare!+Il Signore degli Anelli Tu non puoi passare! Gandalf Film+Film");
        list.add("Un anello per domarli+Il Signore degli Anelli Un anello per domarli trovarli incatenarli gandalf Film+Film");
        list.add("You Shall Not Pass+Il Signore degli Anelli You Shall Not Pass Gandalf Film+Film");
        list.add("300 Questa è Sparta!!!+Others 300 Questa è Sparta Leonida Film+Film");
        list.add("Al mio segnale+Others Al mio segnale scatenate inferno il gladiatore Film+Film");
        list.add("Carlo Verdone In che senso+Others Carlo Verdone In che senso Film+Film");
        list.add("Da un grande potere derivano grandi responsabilità+Others Da un grande potere derivano grandi responsabilità spider man zio ben Film+Film");
        list.add("Dammi la mazza+Others Dammi la mazza wendy shining Film+Film");
        list.add("E.T. Telefono casa+Others E.T. Telefono casa Fantascienza Film+Film");
        list.add("Il Corvo - Non può piovere per sempre+Others Il Corvo - Non può piovere per sempre Crow Film+Film");
        list.add("Nessuno può mettere Baby in un angolo+Others Nessuno può mettere Baby in un angolo Dirty Dancing Film+Film");
        list.add("Non è ancora il momento Pulp Fiction+Others Non è ancora il momento Pulp Fiction pompini Film+Film");
        list.add("Temi, la morte+Others Temi, la morte Davy Jones Capitan Jack Sparrow Film+Film");
        list.add("Un genio... Miliardario, playboy, filantropo Tony Stark+Others Un genio... Miliardario, playboy, filantropo Tony Stark Iron Man Film+Film");
        list.add("Un'offerta che non può rifiutare+Others Un'offerta che non può rifiutare il padrino Film+Film");
        list.add("Grande Giove!+Ritorno al Futuro Grande Giove Doc Film+Film");
        list.add("Strade, dove andiamo noi non ci servono strade+Ritorno al Futuro Strade, dove andiamo noi non ci servono strade Doc marty mcfly Film+Film");
        list.add("Super Stereo+Ritorno al Futuro Super Stereo marty mcfly Film+Film");
        list.add("Anakin tu sottovaluti il mio potere+Star Wars Anakin tu sottovaluti il mio potere skywalker Film+Film");
        list.add("Io sono tuo padre+Star Wars Io sono tuo padre darth vader Film+Film");
        list.add("May the force be with you+Star Wars May the force be with you che la forza sia con te Film+Film");
        list.add("Ah Gay!!!+Una Notte da Leoni Ah Gay!!! ciao cio Film+Film");
        list.add("Fanculo+Una Notte da Leoni Fanculo ciao cio vaffanculo Film+Film");
        list.add("Frullato di Frollini+Una Notte da Leoni Frullato di Frollini alan madre Film+Film");
        list.add("A Genius Renzi+A Genius Renzi Matteo Inglese Politici+Politici");
        list.add("Berlusconi -  Comunisti!+Berlusconi -  Comunisti siete dei poveri Politici+Politici");
        list.add("Berlusconi - Vergogna +Berlusconi - Vergogna Popolo Politici+Politici");
        list.add("Capra+Capra Sgarbi Vittorio politico Politici+Politici");
        list.add("De de de de renzi+De de de de Matteo renzi Inglese Politici+Politici");
        list.add("My mother Crai+My mother Crai shish Matteo renzi inglese Politici+Politici");
        list.add("Time of lunch Renzi+Time of lunch Matteo Renzi inglese Politici+Politici");
        list.add("Barney Stinson - Challenge Accepted+Barney Stinson Barney Stinson - Challenge Accepted sfida accettata how i meet your mother Serie Tv+Serie Tv");
        list.add("Barney stinson - Legen...wait for it...dary+Barney Stinson Barney stinson - Legen...wait for it...dary how i meet your mother leggendario Serie Tv+Serie Tv");
        list.add("Barney stinson - True story+Barney Stinson Barney stinson - True story how i meet your mother storia vera Serie Tv+Serie Tv");
        list.add("Bazinga+Big Bang Theory Bazinga sheldon Serie Tv+Serie Tv");
        list.add("L'ingegneria è....+Big Bang Theory L'ingegneria è Fisica Sheldon Serie Tv+Serie Tv");
        list.add("Penny Penny Penny+Big Bang Theory Penny Penny Penny Sheldon porta bussare Serie Tv+Serie Tv");
        list.add("E allora...+Game of Thrones E allora Il trono di spade - Il matrimonio tra Sansa Stark e Tyrion Lannister scopare Serie Tv+Serie Tv");
        list.add("Hodor+Game of Thrones Hodor il trono di spade Serie Tv+Serie Tv");
        list.add("I am the king Joffrey+Game of Thrones I am the king Joffrey il trono di spade Serie Tv+Serie Tv");
        list.add("Winter is coming+Game of Thrones Winter is coming jon Snow il trono di spade inverno sta arrivando Serie Tv+Serie Tv");
        list.add("You Know Nothing Jon Snow+Game of Thrones You Know Nothing Jon Snow tu non sai niente jon snow il trono di spade Serie Tv+Serie Tv");
        list.add("Bevi famme capì+Gomorra Bevi famme capì fidare di te Gomorra Serie Tv+Serie Tv");
        list.add("Ci penso+Gomorra Ci penso pisciata e ci penso gomorra Serie Tv+Serie Tv");
        list.add("Du frittur+Gomorra Du frittur Serie Tv+Serie Tv");
        list.add("E a me no me ne fott no cazz+Gomorra E a me no me ne fott no cazz Serie Tv+Serie Tv");
        list.add("Eallora stai cuntent+Gomorra Eallora stai cuntent stai contento Serie Tv+Serie Tv");
        list.add("Solo po tiemp+Gomorra Solo po tiemp sparare in bocca tempo perso Serie Tv+Serie Tv");
        list.add("Sta senza pensier+Gomorra Sta senza pensier stai senza pensieri Serie Tv+Serie Tv");
        list.add("Stasera mi sient nu re+Gomorra Stasera mi sient nu re vuoi essere la mia regina Serie Tv+Serie Tv");
        list.add("Strunz+Gomorra Strunz Stronzo Serie Tv+Serie Tv");
        list.add("Pupi Puppi Dewey - Malcolm+Pupi Puppi Dewey Malcolm - Malcolm Serie Tv+Serie Tv");
        list.add("Nooo Dr Cox+Scrubs Nooo Dr Cox Mai Scordatelo Negativo Toglitelo dalla testa Uomo che cade nel burrone Serie tv+Serie Tv");
        list.add("Scrubs Notte Bistecca+Scrubs Scrubs Notte Bistecca Carne di manzo Acquolina in bocca Serie Tv+Serie Tv");
        list.add("Scrubs Sad Song+Scrubs Scrubs Sad Song Triste Serie Tv+Serie Tv");
        list.add("I am the danger+Walter White I am the danger Breaking Bad Serie Tv+Serie Tv");
        list.add("I am the One Who Knocks+Walter White I am the One Who Knocks Breaking Bad Serie Tv+Serie Tv");
        list.add("Say my Name+Walter White Say my Name Breaking Bad Serie Tv+Serie Tv");
        list.add("Destro Secco+Piccinini Destro Secco sandro Goal Calcio+Calcio");
        list.add("Disperatamente con la sciabolata tesa+Piccinini Disperatamente con la sciabolata tesa sandro Goal Calcio+Calcio");
        list.add("Forse c'è stata una deviazione+Piccinini Forse c'è stata una deviazione sandro Goal Calcio+Calcio");
        list.add("Incredibile+Piccinini Incredibile sandro Goal Calcio Siluro Calcio+Calcio");
        list.add("Lui non Sbaglia+Piccinini Lui non Sbaglia sandro Goal Calcio+Calcio");
        list.add("Mucchio Selvaggio+Piccinini Mucchio Selvaggio in aria di rigore ammucchiata sandro Goal Calcio+Calcio");
        list.add("Non Va+Piccinini Non Va sandro Goal Calcio Tiro Calcio+Calcio");
        list.add("Palo Incredibile+Piccinini Palo Incredibile sandro Goal Calcio+Calcio");
        list.add("Proprio Lui+Piccinini Proprio Lui sandro Goal Calcio+Calcio");
        list.add("Rete...+Piccinini Rete... sandro Goal Fantastico Fuoriclasse Calcio+Calcio");
        list.add("Rigore+Piccinini Rigore sandro Goal Calcio Incredibile Calcio+Calcio");
        list.add("Traversa+Piccinini Traversa Incredibile sandro Goal Calcio+Calcio");
        list.add("Il divino parente marino+Calcio Ignoranti Il divino parente marino chi se non lui calcio goal Calcio+Calcio");
        list.add("La punizione di Bonfiglio+Calcio Ignoranti La punizione di Bonfiglio tutto pronto fischio goal calcio non mi viene Calcio+Calcio");
        list.add("Titti, il gol dell'anno+Calcio Ignoranti Titti il gol dell'anno Goal Calcio+Calcio");
        list.add("A me che cazzo me ne frega a me Maccio Capatonda+A me che cazzo me ne frega a me Maccio Capatonda Youtube+Youtube");
        list.add("Abbassa quella luce +Abbassa quella luce Sei un drogato Youtube+Youtube");
        list.add("Abbassa quella voce da gallina+Abbassa quella voce da gallina riccardo il grande Minecraft Youtube+Youtube");
        list.add("Ah porco schifo. È uno sballo, mi piace +Ah porco schifo. È uno sballo, mi piace catone cocktail Youtube+Youtube");
        list.add("Ah quetti bug+Ah quetti bug questi bug babe Youtube+Youtube");
        list.add("Ammazzo 100 persone al secondo+Ammazzo 100 persone al secondo Canzone sento quando Youtube+Youtube");
        list.add("Aspetta Aspetta+Aspetta Aspetta Voldemort Harry Potter Youtube+Youtube");
        list.add("Buonaasssseraaaa Enrico Mentana+Buonaasssseraaaa Enrico Mentana Buonasera Youtube+Youtube");
        list.add("C'ha detto!+C'ha detto Enzo Ostia Roma Poste Italiane Youtube+Youtube");
        list.add("Capitan Jack sparlot+Capitan Jack sparlot Jack Sparrow Youtube+Youtube");
        list.add("Con tutti i miei amici Sweg +Con tutti i miei amici Sweg Swegghiamo gucci boy bello figo gu Youtube+Youtube");
        list.add("Cos'è successo a tu sai chi+Cos'è successo a tu sai chi Hagrid Harry Potter Youtube Voldemort Youtube+Youtube");
        list.add("E se...Hagrid+E se... Hagrid Altri draghetti Draghi Dispetti Funghetti solo un culo Harry Potter Youtube+Youtube");
        list.add("Ea Sportsss+Ea Sportsss ea sports Youtube+Youtube");
        list.add("Enzo non dire parolacce+Enzo non dire parolacce chiamo la polizia Ostia Roma Poste Italiane Youtube+Youtube");
        list.add("Enzo parla bene+Enzo parla bene usa Educazione è chiaro Ostia Roma Poste Italiane Youtube+Youtube");
        list.add("Enzo ti faccio carcerare+Enzo ti faccio carcerare Ostia Roma Poste Italiane Youtube+Youtube");
        list.add("Esatto esatto. Paniccia+Esatto esatto Osvaldo Paniccia Diprè Youtube+Youtube");
        list.add("Fuck her right in a pussy+Fuck her right in a pussy Youtube+Youtube");
        list.add("GTA San Andreas +GTA San Andreas Thug Life Gta Youtube+Youtube");
        list.add("Ho visto tutto col binocchio Maccio Capatonda+Ho visto tutto col binocchio TG Maccio Capatonda binocolo Youtube+Youtube");
        list.add("I Soliti Idioti - Dai Cazzo+I Soliti Idioti - Dai Cazzo Gianluca Ruggero Youtube+Youtube");
        list.add("I Soliti Idioti - Mamma Esco +I Soliti Idioti - Mamma Esco Gianluca Ruggero Youtube+Youtube");
        list.add("Il divino che gioca nel Santos Federico Buffa+Il divino che gioca nel Santos Federico inter jonathan Moreira Federico Buffa Youtube+Youtube");
        list.add("Johnny è divinoooFederico Buffa+Johnny è divinooo divino inter jonathan Moreira Federico Buffa+Youtube");
        list.add("Luca aaacal Canavacciuolo+Luca aaacal Antonino acal Canavacciuolo cucine da incubo Youtube+Youtube");
        list.add("Mamma e papà hanno un nuovo bebè+Mamma e papà hanno un nuovo bebè non se ne fanno piu niente di te dingo Pictures Youtube+Youtube");
        list.add("Mamma, pare che sono santo! Maccio Capatonda+Mamma, pare che sono santo ! Maccio Capatonda sembra TG Youtube+Youtube");
        list.add("Minghie Bello Figo+Minghie Bello Figo Gucci boy Minchie Minchia Youtube+Youtube");
        list.add("Mlg Scream+Mlg Scream Urlo Epico Gente che urla nigga Youtube+Youtube");
        list.add("Monella, monella! Giuseppe Simone+Monella, monella! sei una monella Giuseppe Simone Youtube+Youtube");
        list.add("Mortal Kombat Fatality Sound+Mortal Kombat Fatality Sound fatalità Youtube+Youtube");
        list.add("Mortal Kombat Finish Him Sound+Mortal Kombat Finish Him Sound FIniscilo Youtube+Youtube");
        list.add("Naaa zvegna+Naaa zvegna Na Il Re Leone Alba Tramonto Youtube+Youtube");
        list.add("Nein nein+Nein nein Hitler Fuhrer Youtube+Youtube");
        list.add("Non me ne frega un cazzo+Non me ne frega un cazzo non mi interessa Youtube+Youtube");
        list.add("Nyan cat+Nyan cat Song Gatto Youtube+Youtube");
        list.add("Per me è ancora in circolazione Hagrid+Per me è ancora in circolazione No Per me è Morto Dice Voldemort Harry Potter Hagrid Youtube+Youtube");
        list.add("Porco D..+Porco D.. Allahu Akbar Youtube+Youtube");
        list.add("Quanto cazzo è forte Federico Buffa+Quanto cazzo è forte inter jonathan Moreira 5 minuti Federico Buffa Youtube+Youtube");
        list.add("Richard Benson e il Pollo+Richard Benson e il Pollo viva Youtube+Youtube");
        list.add("Richard Benson, E non mi sta bene!+Richard Benson , E non mi sta bene ! Youtube+Youtube");
        list.add("Rko Randy Orton+Rko Randy Orton whatcha whatcha Youtube+Youtube");
        list.add("Se avessi avuto metà del suo talento Federico Buffa+Se avessi avuto metà del suo talento diventato giocatore forte tempi Inter jonathan Moreira Federico Buffa Youtube+Youtube");
        list.add("Sei speciale per me lo sai+Sei speciale per me lo sai ti amo ciao ciao Youtube+Youtube");
        list.add("Sei un drogato però+Sei un drogato però abbassa quella luce Youtube+Youtube");
        list.add("Shhhhh+Shhhhh sh shh shhh silenzio how to basic Youtube+Youtube");
        list.add("Siete stati visti+Siete stati visti alan non meno di sette 7 babbà piton professor Harry Potter Youtube+Youtube");
        list.add("Signora c'è un uomo per lei +Signora c'è un uomo per lei chi Chang The Lady Youtube+Youtube");
        list.add("Siiii Cristiano Ronaldo+Siiii Si siii sii Cristiano Ronaldo pallone d'oro oro Youtube+Youtube");
        list.add("Soffi Teir!+Soffi Teir partiamo alle sei 6 softair soft air Youtube+Youtube");
        list.add("Sta palleggiando con dei granelli di sabbia Federico Buffa+ jonathan Jonny inter moreira Sta palleggiando con dei granelli di sabbia Federico Buffa Youtube+Youtube");
        list.add("Swaa Bello Figo+Swaa Bello Figo Gu Gucci Boy swag Youtube+Youtube");
        list.add("Swag Swag Swag Bello Figo+Swag Swag Swag Bello Figo Gu Gucci Boy Youtube+Youtube");
        list.add("Taking the hobbits to Isengard+Taking the hobbits to Isengard stanno portando gli hobbit ad legolas il signore degli anelli Youtube+Youtube");
        list.add("Talebabatta+Talebabatta Riccardo il grande minecraft Youtube+Youtube");
        list.add("Ti sborro negli occhi +Ti sborro negli occhi marco neri nero occhio Youtube+Youtube");
        list.add("Trolololo +Trolololo trol face oh oh oh eh eh Youtube+Youtube");
        list.add("Vinooo!!!+Vinooo!!! vino vostra maesta non c'è piu vino robert baratheon il trono di spade game of thrones Youtube+Youtube");
        list.add("Vogliono farmi dottorare+Vogliono farmi dottorare visitare dicono che sia pazzo voldemort tom riddle silente harry potter Youtube+Youtube");
        list.add("Vuoi che muoro +Vuoi che muoro joe bastianich master chef masterchef Youtube+Youtube");
        list.add("Yeeeeeaaaah Csi+Yeeeeeaaaah Csi yeah csi epic orazio the who Won't Fooled Again miami Youtube+Youtube");

        for (String string : list) {
            StringTokenizer st = new StringTokenizer(string, "+");
            String name = st.nextToken();
            String tags = st.nextToken();
            String category = st.nextToken();
            String owner = "admin";

            Category found = null;

            for (Category cat : gs.getCategories()) {
                if (cat.getName().equals(category)) {
                    found = cat;
                    break;
                }
            }

            Recording recording = new Recording(name, name + ".mp3", this, owner);
            recording.setTags(tags);
            recording.setCategory(found);
            if (!found.getRecordings().contains(recording)) {
                found.addRecording(recording);
            }

            DynamoDBTask persister = new DynamoDBTask(this, recording, found, null, null, DynamoDBAction.CATEGORY_AND_RECORDING);
            persister.execute();
        }

    }

    @Override
    public void callback(Object... args) {
        saveAllSounds();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(categorie)) {
            restoreCategories();
        } else if (v.equals(suoni)) {
            if (gs.getCategories() == null || gs.getCategories().isEmpty()) {
                CategoriesAsyncTaskAdmin task = new CategoriesAsyncTaskAdmin(this, this);
                task.execute();
            } else {
                saveAllSounds();
            }
        } else if (v.equals(signInButton)) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } else if (v.equals(signOutButton)) {
            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            // [START_EXCLUDE]
                            updateUI(false);
                            // [END_EXCLUDE]
                        }
                    });
            gs.setAuthenticatedUser(null);
        }
        else if(v.equals(bonifica)){
            if (gs.getCategories() == null || gs.getCategories().isEmpty()) {
                CategoriesAsyncTaskAdmin task = new CategoriesAsyncTaskAdmin(this, new Callable() {
                    @Override
                    public void callback(Object... args) {
                        deleteUnusedFiles();
                    }
                });
                task.execute();
            } else {
                deleteUnusedFiles();
            }
        }
        else if(v.equals(test)){
            getUserFavorites();
        }
    }

    private void getUserFavorites() {

    }

    private void deleteUnusedFiles() {
        Collection<Category> categories = gs.getCategories();
        final Set<String> fileNames = new HashSet<>();
        for (Category category : categories){
            Collection<Recording> recordings = category.getRecordings();
            for (Recording recording : recordings) {
                fileNames.add(recording.getFileName());
            }
        }

        final List<String> s3FileNames = new LinkedList<>();

        S3Task task = new S3Task(this, null, null, new Callable() {
            @Override
            public void callback(Object... args) {
                if(args != null && args.length == 1){
                    List<String> result = (List<String>) args[0];
                    s3FileNames.addAll(result);

                    deleteUnused(fileNames, s3FileNames);
                }
            }
        }, S3Action.LIST);
        task.execute();
    }

    private void deleteUnused(Set<String> used, List<String> searchable) {
        List<String> toDelete = findUnused(used, searchable);
        Toast.makeText(this, "Cancellati "+toDelete.size()+" file", Toast.LENGTH_LONG).show();

        for (String file : toDelete) {
            Recording r = new Recording();
            r.setFileName(file);
            S3Task task = new S3Task(this, r, null, null, S3Action.DELETE);
            task.execute();
        }
    }

    private List<String> findUnused(Set<String> used, List<String> searchable) {
        List<String> toDelete = new LinkedList<>();
        for (String s : searchable) {
            if(!s.startsWith("img") && !used.contains(s)){
                toDelete.add(s);
            }
        }
        return toDelete;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("AAAA", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d("AAAA", "Email: " + acct.getEmail());
            mStatusTextView.setText(acct.getDisplayName());
            updateUI(true);
            SignInTask task = new SignInTask(this, acct.getEmail(), acct.getDisplayName(), null);
            task.execute();
        } else {
            // Signed out, show unauthenticated UI.
            updateUI(false);
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
            signInButton.setVisibility(View.GONE);
            signOutButton.setVisibility(View.VISIBLE);
        } else {
            mStatusTextView.setText(R.string.signed_out);
            signInButton.setVisibility(View.VISIBLE);
            signOutButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d("AAAA", "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.updating));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }
}

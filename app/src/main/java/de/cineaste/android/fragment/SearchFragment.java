package de.cineaste.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

import de.cineaste.android.MainActivity;
import de.cineaste.android.MovieClickListener;
import de.cineaste.android.R;
import de.cineaste.android.adapter.SearchQueryAdapter;
import de.cineaste.android.receiver.NetworkChangeReceiver;
import de.cineaste.android.database.BaseDao;
import de.cineaste.android.entity.Movie;
import de.cineaste.android.network.TheMovieDb;

public class SearchFragment extends Fragment implements MovieClickListener {

    private final TheMovieDb theMovieDb = new TheMovieDb();
    private RecyclerView.Adapter movieQueryAdapter;
    private View view;
    private SearchView searchView;
    private String searchText;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setHasOptionsMenu( true );
    }

    @Override
    public void onSaveInstanceState( Bundle outState ) {
        if( isAdded() ) {
            if( searchView != null ) {
                String searchText = searchView.getQuery().toString();
                if( !TextUtils.isEmpty( searchText ) )
                    outState.putString( "query", searchText );
            }
        }
        super.onSaveInstanceState( outState );
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState ) {

        view = inflater.inflate( R.layout.fragment_search, container, false );

        if( savedInstanceState != null ) {
            searchText = savedInstanceState.getString( "query", null );
        }

        RecyclerView movieQueryRecyclerView = (RecyclerView) view.findViewById( R.id.search_recycler_view );
        RecyclerView.LayoutManager movieQueryLayoutMgr = new LinearLayoutManager( getActivity() );
        movieQueryAdapter = new SearchQueryAdapter( getActivity(), new ArrayList<Movie>(), this );
        movieQueryRecyclerView.setItemAnimator( new DefaultItemAnimator() );

        movieQueryRecyclerView.setLayoutManager( movieQueryLayoutMgr );
        movieQueryRecyclerView.setAdapter( movieQueryAdapter );

        if( !NetworkChangeReceiver.getInstance().isConnected ) {
            Snackbar snackbar = Snackbar
                    .make( view, R.string.noInternet, Snackbar.LENGTH_LONG );
            snackbar.show();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ) {

        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate( R.menu.search_menu, menu );

        MenuItem searchItem = menu.findItem( R.id.action_search );


        if( searchItem != null ) {
            searchView = (SearchView) searchItem.getActionView();
            searchView.setFocusable( true );
            searchView.setIconified( false );
            searchView.requestFocusFromTouch();
            searchView.setOnQueryTextListener( new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit( String query ) {
                    if( !NetworkChangeReceiver.getInstance().isConnected ) {
                        Snackbar snackbar = Snackbar
                                .make( view, R.string.noInternet, Snackbar.LENGTH_LONG );
                        snackbar.show();
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange( String query ) {
                    if( !query.isEmpty() ) {
                        query = query.replace( " ", "+" );
                        if( NetworkChangeReceiver.getInstance().isConnected ) {
                            theMovieDb.searchMoviesAsync( query, new TheMovieDb.OnSearchMoviesResultListener() {
                                @Override
                                public void onSearchMoviesResultListener( List<Movie> movies ) {
                                    ((SearchQueryAdapter) movieQueryAdapter).dataset = movies;
                                    movieQueryAdapter.notifyDataSetChanged();
                                }
                            }, getResources().getString( R.string.language_tag ) );
                        }
                    } else {
                        ((SearchQueryAdapter) movieQueryAdapter).dataset = new ArrayList<>();
                    }
                    return false;
                }

            } );
            if( !TextUtils.isEmpty( searchText ) )
                searchView.setQuery( searchText, false );
        }

        super.onCreateOptionsMenu( menu, inflater );
    }

    @Override
    public void onStop() {
        super.onStop();
        View view = getActivity().getCurrentFocus();
        if( view != null ) {
            InputMethodManager imm =
                    (InputMethodManager) getActivity()
                            .getSystemService( Context.INPUT_METHOD_SERVICE );
            imm.hideSoftInputFromWindow( view.getWindowToken(), 0 );
        }
    }

    //// TODO: 30.12.15 Delete!
    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        switch ( item.getItemId() ) {
            case R.id.startMovieNight:
                break;
        }

        return super.onOptionsItemSelected( item );
    }

    @Override
    public void onMovieClickListener( long movieId ) {
        Bundle bundle = new Bundle();
        bundle.putLong( BaseDao.MovieEntry._ID, movieId );
        MovieDetailsFragment fragment = new MovieDetailsFragment();
        fragment.setArguments( bundle );
        MainActivity.replaceFragment( getFragmentManager(), fragment );
    }
}

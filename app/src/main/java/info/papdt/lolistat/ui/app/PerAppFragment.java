package info.papdt.lolistat.ui.app;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.BaseAdapter;
import android.widget.SearchView;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import info.papdt.lolistat.R;
import info.papdt.lolistat.ui.adapter.AppAdapter;
import info.papdt.lolistat.ui.base.BaseListFragment;
import info.papdt.lolistat.ui.base.GlobalActivity;
import info.papdt.lolistat.ui.model.AppModel;
import static info.papdt.lolistat.ui.utils.UiUtility.*;

public class PerAppFragment extends BaseListFragment<AppModel>
{
	private AppAdapter mAdapter;
	private MenuItem mSearchItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	protected BaseAdapter buildAdapter() {
		mAdapter = new AppAdapter(getActivity(), getItemList());
		return mAdapter;
	}

	@Override
	protected List<AppModel> loadData(ProgressCallback callback) {
		List<AppModel> list = new ArrayList<AppModel>();
		final PackageManager pm = getActivity().getPackageManager();
		List<ApplicationInfo> la = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		
		for (final ApplicationInfo info : la) {
			final AppModel app = new AppModel();
			app.packageName = info.packageName;
			app.title = pm.getApplicationLabel(info).toString();
			app.iconRefreshRunnable = new Runnable() {
				@Override
				public void run() {
					app.icon = new WeakReference<Drawable>(pm.getApplicationIcon(info));
				}
			};
			list.add(app);
			callback.updateProgress(list.size(), la.size());
		}
		
		Collections.sort(list, new Comparator<AppModel>() {
			@Override
			public int compare(AppModel p1, AppModel p2) {
				return Collator.getInstance().compare(p1.title, p2.title);
			}
		});
		
		// Add the Global config entry
		AppModel global = new AppModel();
		global.packageName = "global";
		global.title = getString(R.string.global);
		global.icon = null;
		list.add(0, global);
		
		return list;
	}

	@Override
	protected void onItemClick(int pos) {
		startFragment("peract", mAdapter.getItem(pos).packageName);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.app, menu);
		
		mSearchItem = menu.findItem(R.id.search);
		final SearchView search = $(mSearchItem.getActionView());
		search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
				@Override
				public boolean onQueryTextSubmit(String query) {
					return false;
				}

				@Override
				public boolean onQueryTextChange(String query) {
					mAdapter.getFilter().filter(query);
					return true;
				}
		});
		
		search.setIconified(true);
		
		mSearchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
			@Override
			public boolean onMenuItemActionExpand(MenuItem item) {
				showHomeAsUp();
				search.setIconified(false);
				return true;
			}

			@Override
			public boolean onMenuItemActionCollapse(MenuItem p1) {
				hideHomeAsUp();
				search.setQuery("", false);
				search.setIconified(true);
				return true;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.about:
				startFragment("about");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onReturn() {
		mSearchItem.collapseActionView();
	}

}

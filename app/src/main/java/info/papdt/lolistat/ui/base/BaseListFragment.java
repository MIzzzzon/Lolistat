package info.papdt.lolistat.ui.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import info.papdt.lolistat.R;
import static info.papdt.lolistat.ui.utils.UiUtility.*;

public abstract class BaseListFragment<T> extends BaseFragment 
{
	private ListView mList;
	private List<T> mItemList = new ArrayList<T>();

	@Override
	protected void onFinishInflate(View view) {
		mList = $(view, R.id.list);
		mList.setAdapter(buildAdapter());
		mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
				BaseListFragment.this.onItemClick(pos);
			}
		});
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		new LoadDataTask().execute();
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.list;
	}
	
	protected ListView getListView() {
		return mList;
	}
	
	protected List<T> getItemList() {
		return mItemList;
	}
	
	protected void onItemClick(int pos) {
		
	}
	
	protected void onDataLoaded(List<T> data) {
		mItemList.clear();
		mItemList.addAll(data);
	}
	
	protected abstract BaseAdapter buildAdapter();
	protected abstract List<T> loadData(ProgressCallback callback);
	
	protected interface ProgressCallback {
		void updateProgress(int progress, int max);
	}
	
	private class LoadDataTask extends AsyncTask<Void, Integer, List<T>> {
		private ProgressDialog prog;
		
		@Override
		protected void onPreExecute() {
			prog = new ProgressDialog(getActivity());
			prog.setMessage(getString(R.string.plz_wait));
			prog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			prog.setCancelable(false);
			prog.show();
		}

		@Override
		protected List<T> doInBackground(Void... params) {
			return loadData(new ProgressCallback() {
				@Override
				public void updateProgress(int progress, int max) {
					publishProgress(progress, max);
				}
			});
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			prog.setMax(values[1]);
			prog.setProgress(values[0]);
		}

		@Override
		protected void onPostExecute(List<T> result) {
			prog.dismiss();
			onDataLoaded(result);
			((BaseAdapter) mList.getAdapter()).notifyDataSetChanged();
		}
		
	}
}

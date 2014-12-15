package com.edaviessmith.consumecontent.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.edaviessmith.consumecontent.service.ActionFragment;

public abstract class FragmentStateCachePagerAdapter extends FragmentStatePagerAdapter {
	// Sparse array to keep track of registered fragments in memory
	private SparseArray<ActionFragment> registeredFragments = new SparseArray<ActionFragment>();
 
	public FragmentStateCachePagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}
 
	// Register the fragment when the item is instantiated
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
        ActionFragment fragment = (ActionFragment) super.instantiateItem(container, position);
		registeredFragments.put(position, fragment);
		return fragment;
	}
 
	// Unregister when the item is inactive

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		registeredFragments.remove(position);
        container.removeView(((ActionFragment)object).getView());
		super.destroyItem(container, position, object);
	}


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(((Fragment) object).getView());
    }

    /*@Override
    public boolean isViewFromObject(View view, Object object) {
        View _view = ((Fragment) object).getView();
        return view == _view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        //Remove the view added in instantiateItem from the container
        container.removeView((View)object);

        //delete objects created in instantiateItem (non View classes like Bitmap) if necessary
    }*/

	// Returns the fragment for the position (if instantiated)
	public ActionFragment getRegisteredFragment(int position) {
		return registeredFragments.get(position);
	}
}
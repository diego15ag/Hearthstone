package com.example.diego.hearthstone;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/*
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>  {

    public List<Course> courses;
    public ClickListener clickListener;

    public RecyclerViewAdapter(List<Course> courses) {
        this.courses = courses;
    }


    public void setClickListener(ClickListener clickListener){
        this.clickListener = clickListener;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view =
                LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.layout_row,viewGroup,false);

        return new RecyclerViewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder viewHolder, int i) {


        viewHolder.courseName.setText(courses.get(i).getAsignatura().toString());
        viewHolder.teacherName.setText(courses.get(i).getProfesor().toString());
    }


    @Override
    public int getItemCount() {
        return courses.size();
    }

    public void addCourse(Course course) {
        courses.add(course);
        notifyDataSetChanged();
    }

    protected class ViewHolder
            extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        public TextView courseName;
        public TextView teacherName;

        public ViewHolder(View itemView) {
            super(itemView);

            this.courseName= (TextView) itemView.findViewById(R.id.tvAsignatura);
            this.teacherName= (TextView) itemView.findViewById(R.id.tvProfesor);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            if ( clickListener != null){
                clickListener.itemClicked(v,getPosition());
            }

        }
    }


    public interface ClickListener {
        public void itemClicked(View view,int position);
    }

}*/
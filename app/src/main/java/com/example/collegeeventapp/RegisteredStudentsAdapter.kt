package com.example.collegeeventapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RegisteredStudentsAdapter(
    private val studentList: ArrayList<Registration>
) : RecyclerView.Adapter<RegisteredStudentsAdapter.StudentViewHolder>() {

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvStudentName: TextView =
            itemView.findViewById(R.id.tvStudentName)

        val tvStudentEmail: TextView =
            itemView.findViewById(R.id.tvStudentEmail)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StudentViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_registered_student, parent, false)

        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: StudentViewHolder,
        position: Int
    ) {

        val student = studentList[position]

        holder.tvStudentName.text = student.studentName
        holder.tvStudentEmail.text = student.studentEmail

    }

    override fun getItemCount(): Int {

        return studentList.size

    }
}
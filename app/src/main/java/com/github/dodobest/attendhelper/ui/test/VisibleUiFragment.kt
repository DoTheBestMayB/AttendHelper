package com.github.dodobest.attendhelper.ui.test

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.dodobest.attendhelper.databinding.FragmentVisibleUiBinding

class VisibleUiFragment : Fragment() {

    private var _binding: FragmentVisibleUiBinding? = null
    private val binding: FragmentVisibleUiBinding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisibleUiBinding.inflate(inflater, container, false)

        return binding.root
    }
}
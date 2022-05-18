package com.example.volleyandcurrencyapp.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.volleyandcurrencyapp.databinding.ItemCurrencyBinding
import com.example.volleyandcurrencyapp.models.Currency

class CurrencyAdapter : RecyclerView.Adapter<CurrencyAdapter.VH>() {

    private var onItemClick: OnItemClick? = null
    private var list: List<Currency>? = null

    fun setAdapter(list: List<Currency>) {
        this.list = list
    }

    inner class VH(var binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBind(currency: Currency) {
            binding.name.text = currency.CcyNm_UZ

            binding.root.setOnClickListener {
                if (onItemClick != null) {
                    onItemClick?.onClick(currency)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(list!![position])
    }

    override fun getItemCount(): Int = list!!.size

    interface OnItemClick {
        fun onClick(currency: Currency)
    }

    fun setOnItemClick(onItemClick: OnItemClick) {
        this.onItemClick = onItemClick
    }
}
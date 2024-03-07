//
//  ContentView.swift
//  note
//
//  Created by Fn 鹅 on 2024/3/3.
//

import SwiftUI
import SwiftData
import FeatureNote

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> some UIViewController {
        NoteAppViewControllerKt.NoteAppViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
        
    }
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
    }
}

#Preview {
    ContentView()
}

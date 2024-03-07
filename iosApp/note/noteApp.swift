//
//  noteApp.swift
//  note
//
//  Created by Fn 鹅 on 2024/3/3.
//

import SwiftUI
import SwiftData
import FeatureNote

@main
struct noteApp: App {
    
    init() {
        GooseNoteKoinKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}

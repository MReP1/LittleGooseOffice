//
//  Item.swift
//  note
//
//  Created by Fn 鹅 on 2024/3/3.
//

import Foundation
import SwiftData

@Model
final class Item {
    var timestamp: Date
    
    init(timestamp: Date) {
        self.timestamp = timestamp
    }
}
